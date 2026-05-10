package com.skhatoll.backend.service.impl.partida;

import com.skhatoll.backend.dto.partida.*;
import com.skhatoll.backend.dto.sala.JugadorDto;
import com.skhatoll.backend.entities.*;
import com.skhatoll.backend.repository.*;
import com.skhatoll.backend.service.impl.sala.SalaSocketService;
import com.skhatoll.backend.service.interfaces.partida.IPartidaService;
import com.skhatoll.backend.service.interfaces.jugador.IJugadorService;
import com.skhatoll.backend.util.constants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.skhatoll.backend.util.constants.ErrorMessages.*;
import static com.skhatoll.backend.util.constants.GameConstants.*;

@Service
@RequiredArgsConstructor
public class PartidaService implements IPartidaService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final SesionVotacionRepository sesionVotacionRepository;
    private final VotoRepository votoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PartidaSocketService partidaSocketService;
    private final SalaSocketService salaSocketService;
    private final IJugadorService jugadorService;
    private final HabilidadService habilidadService;

    // -------------------------------------------------------
    // Obtener el usuario autenticado desde el contexto de Security
    // -------------------------------------------------------
    private Usuario getUsuarioAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        String nombre = authentication.getName();

        return usuarioRepository.findByNombre(nombre).orElseThrow(() -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO + ": " + nombre));
    }

    // -------------------------------------------------------
    // Obtener sala iniciada por código
    // -------------------------------------------------------
    private Sala getSalaIniciada(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));

        if (sala.getEstadoSala() != Sala.EstadoSala.INICIADA) {
            throw new IllegalStateException(PARTIDA_NO_INICIADA);
        }

        return sala;
    }

    // -------------------------------------------------------
    // Cambiar fase DÍA/NOCHE
    // -------------------------------------------------------
    @Transactional
    public void cambiarFase(String codigoSala) {
        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        if (sesionVotacionRepository.existsBySala_IdSalaAndAbiertaTrue(sala.getIdSala())) {
            throw new IllegalStateException("Debes cerrar la votación antes de cambiar la fase");
        }

        Sala.EstadoDia nuevaFase = sala.getEstadoDia() == Sala.EstadoDia.DIA ? Sala.EstadoDia.NOCHE : Sala.EstadoDia.DIA;

        if (nuevaFase == Sala.EstadoDia.DIA) {
            sala.setRondaActual(sala.getRondaActual() + 1);
            
            List<SalaUsuario> semiMuertos = salaUsuarioRepository.findBySala_IdSalaAndEstaVivoFalse(sala.getIdSala());
            for (SalaUsuario su : semiMuertos) {
                if (su.getMuerteConfirmada() == null || !su.getMuerteConfirmada()) {
                    su.setMuerteConfirmada(true);
                    salaUsuarioRepository.save(su);
                    
                    String nombreEliminado = su.getUsuario().getNombre();
                    String nombreRol = su.getRol() != null ? su.getRol().getNombre() : " aldeano";
                    String bando = su.getRol() != null ? su.getRol().getBando().name() : "aldea";
                    
                    partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(nombreEliminado, nombreRol, bando));
                }
            }
        }

        sala.setEstadoDia(nuevaFase);
        salaRepository.save(sala);

        partidaSocketService.notificarCambioFase(codigoSala, nuevaFase);
    }

    // -------------------------------------------------------
    // Abrir sesión de votación
    // -------------------------------------------------------
    @Transactional
    public SesionVotacion abrirVotacion(String codigoSala, AbrirVotacionRequest request) {
        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        if (sesionVotacionRepository.existsBySala_IdSalaAndAbiertaTrue(sala.getIdSala())) {
            throw new IllegalStateException(VOTACION_YA_ABIERTA);
        }

        SesionVotacion sesion = SesionVotacion.builder().sala(sala).tipo(request.getTipo()).ronda(sala.getRondaActual()).build();

        sesionVotacionRepository.save(sesion);

        partidaSocketService.notificarVotacion(codigoSala, sesion.getIdSesion(), sesion.getTipo().name(), true);

        return sesion;
    }

    // -------------------------------------------------------
    // Cerrar sesión de votación y calcular resultado
    // -------------------------------------------------------
    @Transactional
    public ResultadoVotacionDto cerrarVotacion(String codigoSala, Integer idSesion) {
        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        SesionVotacion sesion = sesionVotacionRepository.findById(idSesion).orElseThrow(() -> new IllegalArgumentException("Sesión de votación no encontrada"));

        if (!sesion.getAbierta()) {
            throw new IllegalStateException("La sesión ya está cerrada");
        }

        sesion.setAbierta(false);
        sesion.setFechaCierre(LocalDateTime.now());
        sesionVotacionRepository.save(sesion);

        ResultadoVotacionDto resultado = calcularResultado(sesion);

        // Elección de alcalde: asignar ganador como nuevo alcalde
        if (sesion.getTipo() == SesionVotacion.TipoVotacion.ALCALDE && !resultado.isEmpate() && resultado.getNombreGanador() != null) {

            usuarioRepository.findByNombre(resultado.getNombreGanador()).ifPresent(nuevoAlcalde -> {
                sala.setAlcalde(nuevoAlcalde);
                salaRepository.save(sala);
            });

            salaSocketService.notificarAlcalde(codigoSala, resultado.getNombreGanador());
        }

        if (sesion.getTipo() == SesionVotacion.TipoVotacion.DIA && !resultado.isEmpate() && resultado.getNombreEliminado() != null) {

            usuarioRepository.findByNombre(resultado.getNombreEliminado()).ifPresent(eliminado -> confirmarMuerte(codigoSala, eliminado.getIdUsuario()));
        }
        if (sesion.getTipo() == SesionVotacion.TipoVotacion.LOBOS
                && !resultado.isEmpate()
                && resultado.getNombreEliminado() != null) {

            usuarioRepository.findByNombre(resultado.getNombreEliminado())
                    .ifPresent(victima -> {
                        salaUsuarioRepository
                                .findBySala_IdSalaAndUsuario_IdUsuario(
                                        sala.getIdSala(), victima.getIdUsuario())
                                .ifPresent(su -> {
                                    su.setEstaVivo(false);
                                    su.setMuerteConfirmada(false);
                                    salaUsuarioRepository.save(su);
                                });
                    });
        }

        if (sesion.getTipo() == SesionVotacion.TipoVotacion.LOBOS && !resultado.isEmpate() && resultado.getNombreEliminado() != null) {

            usuarioRepository.findByNombre(resultado.getNombreEliminado()).flatMap(victima -> salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), victima.getIdUsuario())).ifPresent(su -> {
                su.setEstaVivo(false);
                su.setMuerteConfirmada(false);
                salaUsuarioRepository.save(su);

                partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(su.getUsuario().getNombre(), su.getRol().getNombre(), su.getRol().getBando().name()));
            });
        }

        partidaSocketService.notificarVotacion(codigoSala, sesion.getIdSesion(), sesion.getTipo().name(), false);
        partidaSocketService.notificarResultadoVotacion(codigoSala, resultado);

        return resultado;
    }

    // -------------------------------------------------------
    // Emitir o cambiar voto (upsert)
    // -------------------------------------------------------
    @Transactional
    public void votar(String codigoSala, VotarRequest request) {
        Usuario votante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        SesionVotacion sesion = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).orElseThrow(() -> new IllegalStateException(VOTACION_NO_ENCONTRADA));

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), votante.getIdUsuario()).orElseThrow(() -> new IllegalStateException(JUGADOR_NO_EN_SALA));

        if (!salaUsuario.getEstaVivo()) {
            throw new IllegalStateException(JUGADOR_ELIMINADO);
        }

        Usuario objetivo = usuarioRepository.findById(request.getIdObjetivo()).orElseThrow(() -> new IllegalArgumentException("Jugador objetivo no encontrado"));

        SalaUsuario objetivoEnSala = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivo.getIdUsuario()).orElseThrow(() -> new IllegalArgumentException("El objetivo no está en esta sala"));

        if (!objetivoEnSala.getEstaVivo()) {
            throw new IllegalStateException("No puedes votar a un jugador eliminado");
        }

        if (votante.getIdUsuario().equals(objetivo.getIdUsuario())) {
            throw new IllegalStateException("No puedes votarte a ti mismo");
        }

        Optional<Voto> votoExistente = votoRepository.findBySesion_IdSesionAndVotante_IdUsuario(sesion.getIdSesion(), votante.getIdUsuario());

        Voto voto;
        if (votoExistente.isPresent()) {
            voto = votoExistente.get();
            voto.setObjetivo(objetivo);
            voto.setFechaVoto(LocalDateTime.now());
        } else {
            voto = Voto.builder().sesion(sesion).votante(votante).objetivo(objetivo).build();
        }

        votoRepository.save(voto);

        List<VotoDto> votos = votoRepository.findBySesion_IdSesion(sesion.getIdSesion()).stream().map(v -> new VotoDto(v.getVotante().getNombre(), v.getObjetivo().getNombre())).toList();

        partidaSocketService.notificarVotos(codigoSala, votos);
    }

    // -------------------------------------------------------
    // Confirmar muerte de un jugador
    // -------------------------------------------------------
    @Transactional
    public void confirmarMuerte(String codigoSala, Integer idUsuario) {
        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idUsuario).orElseThrow(() -> new IllegalArgumentException("El jugador no está en esta sala"));

        if (!salaUsuario.getEstaVivo()) {
            throw new IllegalStateException("El jugador ya está eliminado");
        }
        if (salaUsuario.getMuerteConfirmada()) {
            throw new IllegalStateException("La muerte de este jugador ya fue confirmada");
        }

        salaUsuario.setEstaVivo(false);
        salaUsuario.setMuerteConfirmada(true);
        salaUsuarioRepository.save(salaUsuario);

        if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(idUsuario)) {
            sala.setAlcalde(null);
            salaRepository.save(sala);
            salaSocketService.notificarAlcalde(codigoSala, null);
        }

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(salaUsuario.getUsuario().getNombre(), salaUsuario.getRol().getNombre(), salaUsuario.getRol().getBando().name());

        partidaSocketService.notificarMuerte(codigoSala, muerte);

        salaUsuarioRepository.findBySala_IdSalaAndIdModelo(sala.getIdSala(), idUsuario).ifPresent(nino -> {
            rolRepository.findByNombre(ROL_LOBO).ifPresent(rolLobo -> {
                nino.setRol(rolLobo);
                salaUsuarioRepository.save(nino);

                salaSocketService.enviarRolPrivado(nino.getUsuario().getNombre(), new SalaSocketService.RolAsignadoEvent(WS_EVENTO_ROL_CAMBIADO, rolLobo.getIdRol(), rolLobo.getNombre(), rolLobo.getDescripcion(), rolLobo.getBando().name()));

                List<String> companerosLobos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream().filter(su -> su.getEstaVivo() && su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo && !su.getUsuario().getIdUsuario().equals(nino.getUsuario().getIdUsuario())).map(su -> su.getUsuario().getNombre()).toList();

                salaSocketService.enviarMensajePrivado(nino.getUsuario().getNombre(), "/queue/lobos", new SalaSocketService.CompañerosLobosEvent(WS_EVENTO_COMPANEROS_LOBOS, companerosLobos));
            });
        });

        comprobarFinPartida(codigoSala, sala);
    }

    // -------------------------------------------------------
    // Comprobar si la partida ha terminado
    // -------------------------------------------------------
    private void comprobarFinPartida(String codigoSala, Sala sala) {
        List<SalaUsuario> jugadoresVivos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream().filter(su -> su.getEstaVivo() && !su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario())).toList();

        long lobosVivos = jugadoresVivos.stream().filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo).count();

        long aldeaViva = jugadoresVivos.stream().filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.aldea).count();

        String bandoGanador = null;
        String mensaje = null;

        if (lobosVivos == 0) {
            bandoGanador = BANDO_ALDEA;
            mensaje = "¡La aldea ha eliminado a todos los hombres lobo!";
        } else if (lobosVivos >= aldeaViva) {
            bandoGanador = BANDO_LOBO;
            mensaje = "¡Los hombres lobo dominan la aldea!";
        }

        if (bandoGanador != null) {
            sala.setEstadoSala(Sala.EstadoSala.CERRADA);
            salaRepository.save(sala);
            partidaSocketService.notificarFinPartida(codigoSala, new FinPartidaDto(bandoGanador, mensaje));
        }
    }

    // -------------------------------------------------------
    // Calcular resultado de una votación cerrada
    // -------------------------------------------------------
    private ResultadoVotacionDto calcularResultado(SesionVotacion sesion) {
        List<Voto> votos = votoRepository.findBySesion_IdSesion(sesion.getIdSesion());

        if (votos.isEmpty()) {
            return new ResultadoVotacionDto(sesion.getIdSesion(), sesion.getTipo().name(), null, null, true);
        }

        Map<String, Long> conteo = votos.stream().collect(Collectors.groupingBy(v -> v.getObjetivo().getNombre(), Collectors.summingLong(v -> {
            boolean esAlcalde = sesion.getSala().getAlcalde() != null && v.getVotante().getIdUsuario().equals(sesion.getSala().getAlcalde().getIdUsuario());
            return esAlcalde ? 2L : 1L;
        })));

        long maxVotos = conteo.values().stream().mapToLong(Long::longValue).max().orElse(0);

        List<String> masVotados = conteo.entrySet().stream().filter(e -> e.getValue() == maxVotos).map(Map.Entry::getKey).toList();

        if (masVotados.size() > 1) {
            Usuario alcalde = sesion.getSala().getAlcalde();

            if (alcalde != null) {
                Optional<Voto> votoAlcalde = votos.stream().filter(v -> v.getVotante().getIdUsuario().equals(alcalde.getIdUsuario())).findFirst();

                if (votoAlcalde.isPresent()) {
                    String elegido = votoAlcalde.get().getObjetivo().getNombre();

                    if (sesion.getTipo() == SesionVotacion.TipoVotacion.ALCALDE) {
                        return new ResultadoVotacionDto(sesion.getIdSesion(), sesion.getTipo().name(), null, elegido, false);
                    }

                    return new ResultadoVotacionDto(sesion.getIdSesion(), sesion.getTipo().name(), elegido, null, false);
                }
            }

            return new ResultadoVotacionDto(sesion.getIdSesion(), sesion.getTipo().name(), null, null, true);
        }

        String nombreGanador = masVotados.getFirst();

        if (sesion.getTipo() == SesionVotacion.TipoVotacion.ALCALDE) {
            return new ResultadoVotacionDto(sesion.getIdSesion(), sesion.getTipo().name(), null, nombreGanador, false);
        }

        return new ResultadoVotacionDto(sesion.getIdSesion(), sesion.getTipo().name(), nombreGanador, null, false);
    }

    // -------------------------------------------------------
    // Obtener sesión de votación activa de una sala
    // -------------------------------------------------------
    public SesionVotacion getSesionActiva(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));

        return sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).orElseThrow(() -> new IllegalStateException(VOTACION_NO_ENCONTRADA));
    }

    @Transactional
    public void cerrarPartida(String codigoSala) {
        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(ErrorMessages.SOLO_NARRADOR);
        }

        sala.setEstadoSala(Sala.EstadoSala.CERRADA);
        salaRepository.save(sala);

        partidaSocketService.notificarFinPartida(codigoSala, new FinPartidaDto(null, "Partida cerrada por el narrador"));
    }

    public EstadoPartidaDto getEstadoPartida(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SALA_NO_ENCONTRADA));

        if (sala.getEstadoSala() != Sala.EstadoSala.INICIADA) {
            throw new IllegalStateException(ErrorMessages.PARTIDA_NO_INICIADA);
        }

        List<JugadorDto> jugadores = jugadorService.getJugadores(codigoSala);

        SesionActivaDto sesionActiva = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).map(s -> new SesionActivaDto(s.getIdSesion(), s.getTipo().name(), s.getRonda(), s.getFechaInicio())).orElse(null);

        String nombreAlcalde = sala.getAlcalde() != null ? sala.getAlcalde().getNombre() : null;

        return new EstadoPartidaDto(sala.getEstadoDia().name(), sala.getRondaActual(), jugadores, sesionActiva, nombreAlcalde);
    }
}
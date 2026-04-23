package com.skhatoll.backend.service.impl.partida;

import com.skhatoll.backend.dto.partida.*;
import com.skhatoll.backend.entities.*;
import com.skhatoll.backend.repository.*;
import com.skhatoll.backend.service.impl.sala.SalaSocketService;
import com.skhatoll.backend.service.interfaces.partida.IPartidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartidaService implements IPartidaService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final SesionVotacionRepository sesionVotacionRepository;
    private final VotoRepository votoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PartidaSocketService partidaSocketService;
    private final SalaSocketService salaSocketService;
    private final HabilidadSalaRepository habilidadSalaRepository;
    private final EnamoradosRepository enamoradosRepository;

    // -------------------------------------------------------
    // Obtener el usuario autenticado desde el contexto de Security
    // -------------------------------------------------------
    private Usuario getUsuarioAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        String nombre = authentication.getName();

        return usuarioRepository.findByNombre(nombre).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + nombre));
    }

    // -------------------------------------------------------
    // Obtener sala iniciada por código
    // -------------------------------------------------------
    private Sala getSalaIniciada(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        if (sala.getEstadoSala() != Sala.EstadoSala.INICIADA) {
            throw new IllegalStateException("La partida no está en curso");
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
            throw new IllegalStateException("Solo el narrador puede cambiar la fase");
        }

        if (sesionVotacionRepository.existsBySala_IdSalaAndAbiertaTrue(sala.getIdSala())) {
            throw new IllegalStateException("Debes cerrar la votación antes de cambiar la fase");
        }

        Sala.EstadoDia nuevaFase = sala.getEstadoDia() == Sala.EstadoDia.DIA ? Sala.EstadoDia.NOCHE : Sala.EstadoDia.DIA;

        if (nuevaFase == Sala.EstadoDia.DIA) {
            sala.setRondaActual(sala.getRondaActual() + 1);
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
            throw new IllegalStateException("Solo el narrador puede abrir votaciones");
        }

        if (sesionVotacionRepository.existsBySala_IdSalaAndAbiertaTrue(sala.getIdSala())) {
            throw new IllegalStateException("Ya hay una votación abierta en esta sala");
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
            throw new IllegalStateException("Solo el narrador puede cerrar votaciones");
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

        // Linchamiento automático: si es votación de día con ganador claro, confirmar muerte
        if (sesion.getTipo() == SesionVotacion.TipoVotacion.DIA && !resultado.isEmpate() && resultado.getNombreEliminado() != null) {

            usuarioRepository.findByNombre(resultado.getNombreEliminado()).ifPresent(eliminado -> confirmarMuerte(codigoSala, eliminado.getIdUsuario()));
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

        SesionVotacion sesion = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).orElseThrow(() -> new IllegalStateException("No hay ninguna votación abierta"));

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), votante.getIdUsuario()).orElseThrow(() -> new IllegalStateException("No estás en esta sala"));

        if (!salaUsuario.getEstaVivo()) {
            throw new IllegalStateException("Los jugadores eliminados no pueden votar");
        }

        Usuario objetivo = usuarioRepository.findById(request.getIdObjetivo()).orElseThrow(() -> new IllegalArgumentException("Jugador objetivo no encontrado"));

        SalaUsuario objetivoEnSala = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivo.getIdUsuario()).orElseThrow(() -> new IllegalArgumentException("El objetivo no está en esta sala"));

        if (!objetivoEnSala.getEstaVivo()) {
            throw new IllegalStateException("No puedes votar a un jugador eliminado");
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
            throw new IllegalStateException("Solo el narrador puede confirmar muertes");
        }

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idUsuario).orElseThrow(() -> new IllegalArgumentException("El jugador no está en esta sala"));

        if (!salaUsuario.getEstaVivo()) {
            throw new IllegalStateException("El jugador ya está eliminado");
        }

        salaUsuario.setEstaVivo(false);
        salaUsuario.setMuerteConfirmada(true);
        salaUsuarioRepository.save(salaUsuario);

        // Si muere el alcalde → desasignarlo y notificar
        if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(idUsuario)) {
            sala.setAlcalde(null);
            salaRepository.save(sala);
            salaSocketService.notificarAlcalde(codigoSala, null);
        }

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(salaUsuario.getUsuario().getNombre(), salaUsuario.getRol().getNombre(), salaUsuario.getRol().getBando().name());

        partidaSocketService.notificarMuerte(codigoSala, muerte);

        comprobarFinPartida(codigoSala, sala);
    }

    // -------------------------------------------------------
    // Comprobar si la partida ha terminado
    // -------------------------------------------------------
    private void comprobarFinPartida(String codigoSala, Sala sala) {
        List<SalaUsuario> jugadoresVivos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream().filter(su -> su.getEstaVivo() && !su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario())).toList();

        long lobosVivos = jugadoresVivos.stream().filter(su -> su.getRol() != null && su.getRol().getBando().name().equals("lobo")).count();

        long aldeaViva = jugadoresVivos.stream().filter(su -> su.getRol() != null && su.getRol().getBando().name().equals("aldea")).count();

        String bandoGanador = null;
        String mensaje = null;

        if (lobosVivos == 0) {
            bandoGanador = "aldea";
            mensaje = "¡La aldea ha eliminado a todos los hombres lobo!";
        } else if (lobosVivos >= aldeaViva) {
            bandoGanador = "lobo";
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

        // El voto del alcalde cuenta doble
        Map<String, Long> conteo = votos.stream().collect(Collectors.groupingBy(v -> v.getObjetivo().getNombre(), Collectors.summingLong(v -> {
            boolean esAlcalde = sesion.getSala().getAlcalde() != null && v.getVotante().getIdUsuario().equals(sesion.getSala().getAlcalde().getIdUsuario());
            return esAlcalde ? 2L : 1L;
        })));

        long maxVotos = conteo.values().stream().mapToLong(Long::longValue).max().orElse(0);

        List<String> masVotados = conteo.entrySet().stream().filter(e -> e.getValue() == maxVotos).map(Map.Entry::getKey).toList();

        // Empate: intentar desempate por voto del alcalde
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

        // Ganador claro: asignar a nombreEliminado o nombreGanador según el tipo de votación
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
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        return sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).orElseThrow(() -> new IllegalStateException("No hay ninguna votación abierta"));
    }

    @Transactional
    public HabilidadResultadoDto usarHabilidad(String codigoSala, HabilidadRequest request) {
        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);


        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), solicitante.getIdUsuario()).orElseThrow(() -> new IllegalArgumentException("No estás en esta sala"));

        if (!salaUsuario.getEstaVivo()) {
            throw new IllegalStateException("Los jugadores eliminados no pueden usar habilidades");
        }


        SesionVotacion sesion = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).orElseThrow(() -> new IllegalStateException("No hay ninguna votación abierta"));

        if (sesion.getTipo() != SesionVotacion.TipoVotacion.HABILIDAD) {
            throw new IllegalStateException("La sesión activa no es de tipo HABILIDAD");
        }

        String nombreHabilidad = request.getNombreHabilidad();


        if ("vision".equals(nombreHabilidad)) {
            return usarVision(sala, salaUsuario, request.getObjetivos());
        }


        HabilidadSala habilidad = habilidadSalaRepository.findByIdSalaUsuario_IdSalaUsuarioAndNombre(salaUsuario.getIdSalaUsuario(), nombreHabilidad).orElseThrow(() -> new IllegalArgumentException("No tienes la habilidad: " + nombreHabilidad));

        if (habilidad.getUsada()) {
            throw new IllegalStateException("Habilidad ya utilizada");
        }


        HabilidadResultadoDto resultado = switch (nombreHabilidad) {
            case "pocion_vida" -> usarPocionVida(sala, request.getObjetivos());
            case "pocion_muerte" -> usarPocionMuerte(codigoSala, sala, request.getObjetivos());
            case "disparo" -> usarDisparo(codigoSala, sala, salaUsuario, request.getObjetivos());
            case "flechazo" -> usarFlechazo(sala, request.getObjetivos());
            default -> throw new IllegalArgumentException("Habilidad desconocida: " + nombreHabilidad);
        };


        habilidad.setUsada(true);
        habilidadSalaRepository.save(habilidad);

        return resultado;
    }

    // -------------------------------------------------------
    // Poción de vida: revive a un jugador eliminado
    // -------------------------------------------------------
    private HabilidadResultadoDto usarPocionVida(Sala sala, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("La poción de vida requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst()).orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (objetivo.getEstaVivo()) {
            throw new IllegalStateException("El jugador ya está vivo");
        }

        objetivo.setEstaVivo(true);
        objetivo.setMuerteConfirmada(false);
        salaUsuarioRepository.save(objetivo);

        return new HabilidadResultadoDto("pocion_vida", List.of(objetivo.getUsuario().getNombre()), "REVIVIDO", null);
    }

    // -------------------------------------------------------
    // Poción de muerte: elimina a un jugador vivo
    // -------------------------------------------------------
    private HabilidadResultadoDto usarPocionMuerte(String codigoSala, Sala sala, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("La poción de muerte requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst()).orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!objetivo.getEstaVivo()) {
            throw new IllegalStateException("El jugador ya está eliminado");
        }

        objetivo.setEstaVivo(false);
        objetivo.setMuerteConfirmada(true);
        salaUsuarioRepository.save(objetivo);

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(objetivo.getUsuario().getNombre(), objetivo.getRol().getNombre(), objetivo.getRol().getBando().name());

        partidaSocketService.notificarMuerte(codigoSala, muerte);
        comprobarFinPartida(codigoSala, sala);

        return new HabilidadResultadoDto("pocion_muerte", List.of(objetivo.getUsuario().getNombre()), "ELIMINADO", null);
    }

    // -------------------------------------------------------
    // Disparo del cazador: elimina a un jugador al morir
    // -------------------------------------------------------
    private HabilidadResultadoDto usarDisparo(String codigoSala, Sala sala,
                                              SalaUsuario cazador,
                                              List<Integer> objetivos) {
        if (cazador.getEstaVivo()) {
            throw new IllegalStateException("El cazador solo puede disparar cuando ha sido eliminado");
        }

        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("El disparo requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository
                .findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!objetivo.getEstaVivo()) {
            throw new IllegalStateException("El objetivo ya está eliminado");
        }

        objetivo.setEstaVivo(false);
        objetivo.setMuerteConfirmada(true);
        salaUsuarioRepository.save(objetivo);

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(
                objetivo.getUsuario().getNombre(),
                objetivo.getRol().getNombre(),
                objetivo.getRol().getBando().name());

        partidaSocketService.notificarMuerte(codigoSala, muerte);
        comprobarFinPartida(codigoSala, sala);

        return new HabilidadResultadoDto(
                "disparo",
                List.of(objetivo.getUsuario().getNombre()),
                "ELIMINADO",
                null);
    }

    // -------------------------------------------------------
    // Flechazo de Cupido: vincula dos jugadores como enamorados
    // -------------------------------------------------------
    private HabilidadResultadoDto usarFlechazo(Sala sala, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 2) {
            throw new IllegalArgumentException("El flechazo requiere exactamente dos objetivos");
        }

        Usuario u1 = usuarioRepository.findById(objetivos.get(0)).orElseThrow(() -> new IllegalArgumentException("Jugador 1 no encontrado"));
        Usuario u2 = usuarioRepository.findById(objetivos.get(1)).orElseThrow(() -> new IllegalArgumentException("Jugador 2 no encontrado"));


        salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), u1.getIdUsuario()).filter(SalaUsuario::getEstaVivo).orElseThrow(() -> new IllegalStateException("El jugador 1 no está vivo en la sala"));

        salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), u2.getIdUsuario()).filter(SalaUsuario::getEstaVivo).orElseThrow(() -> new IllegalStateException("El jugador 2 no está vivo en la sala"));

        Enamorados enamorados = Enamorados.builder().sala(sala).usuario1(u1).usuario2(u2).build();

        enamoradosRepository.save(enamorados);

        return new HabilidadResultadoDto("flechazo", List.of(u1.getNombre(), u2.getNombre()), "ENAMORADOS", null);
    }

    // -------------------------------------------------------
    // Visión de la Vidente: revela el rol de un jugador (solo al solicitante)
    // -------------------------------------------------------
    private HabilidadResultadoDto usarVision(Sala sala, SalaUsuario vidente, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("La visión requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst()).orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!objetivo.getEstaVivo()) {
            throw new IllegalStateException("No puedes usar la visión sobre un jugador eliminado");
        }

        if (objetivo.getRol() == null) {
            throw new IllegalStateException("El jugador no tiene rol asignado");
        }


        var detalle = Map.of("nombreRol", objetivo.getRol().getNombre(), "bando", objetivo.getRol().getBando().name());

        return new HabilidadResultadoDto("vision", List.of(objetivo.getUsuario().getNombre()), "ROL_REVELADO", detalle);
    }
}
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.skhatoll.backend.util.constants.ErrorMessages.*;
import static com.skhatoll.backend.util.constants.GameConstants.*;
import com.skhatoll.backend.entities.Enamorados;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartidaService implements IPartidaService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final SesionVotacionRepository sesionVotacionRepository;
    private final VotoRepository votoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnamoradosRepository enamoradosRepository;
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
        log.info("Narrador {} cambiando fase en sala {}", solicitante.getNombre(), codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            log.warn("Usuario {} tentou cambiar fase sin ser narrador en sala {}", solicitante.getNombre(), codigoSala);
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        if (sesionVotacionRepository.existsBySala_IdSalaAndAbiertaTrue(sala.getIdSala())) {
            log.warn("No se puede cambiar fase: hay votacion abierta en sala {}", codigoSala);
            throw new IllegalStateException("Debes cerrar la votación antes de cambiar la fase");
        }

        Sala.EstadoDia nuevaFase = sala.getEstadoDia() == Sala.EstadoDia.DIA ? Sala.EstadoDia.NOCHE : Sala.EstadoDia.DIA;

        if (nuevaFase == Sala.EstadoDia.DIA) {
            log.info("=== TRANSICION NOCHE->DIA en sala {} ===", codigoSala);
            sala.setRondaActual(sala.getRondaActual() + 1);
            
            Optional<Enamorados> enamoradoOpt = enamoradosRepository.findByIdSala(sala.getIdSala());
            List<Integer> idsEnamorados = enamoradoOpt.map(e -> 
                List.of(e.getUsuario1().getIdUsuario(), e.getUsuario2().getIdUsuario())
            ).orElse(List.of());
            log.info("idsEnamorados: {}", idsEnamorados);
            
            List<SalaUsuario> semiMuertos = salaUsuarioRepository.findBySala_IdSalaAndEstaVivoFalse(sala.getIdSala());
            log.info("semiMuertos encontrados: {}", semiMuertos.stream().map(su -> su.getUsuario().getNombre()).toList());
            
            for (SalaUsuario su : semiMuertos) {
                log.info("Procesando {} - estaVivo: {}, muerteConfirmada: {}", su.getUsuario().getNombre(), su.getEstaVivo(), su.getMuerteConfirmada());
                
                if (su.getMuerteConfirmada() == null || !su.getMuerteConfirmada()) {
                    su.setMuerteConfirmada(true);
                    salaUsuarioRepository.save(su);
                    
                    String nombreEliminado = su.getUsuario().getNombre();
                    String nombreRol = su.getRol() != null ? su.getRol().getNombre() : " aldeano";
                    String bando = su.getRol() != null ? su.getRol().getBando().name() : "aldea";
                    
                    log.info("Confirmando muerte de {} ({}) en sala {}", nombreEliminado, nombreRol, codigoSala);
                    partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(nombreEliminado, nombreRol, bando, true, MuerteConfirmadaDto.TIPO_CONFIRMAR));
                    
                    if (!idsEnamorados.isEmpty() && idsEnamorados.contains(su.getUsuario().getIdUsuario())) {
                        log.info("*** ENAMORADO {} MUERTO - MATANDO A SU PAREJA ***", su.getUsuario().getNombre());
                        Integer idPareja = idsEnamorados.get(0).equals(su.getUsuario().getIdUsuario()) 
                            ? idsEnamorados.get(1) : idsEnamorados.get(0);
                        log.info("ID pareja a matar: {}", idPareja);
                        
                        var parejaOpt = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idPareja);
                        log.info("Pareja encontrada: {}", parejaOpt.isPresent());
                        
                        parejaOpt.ifPresent(pareja -> {
                            log.info("Matando a pareja {} - estaVivo: {}, muerteConfirmada: {}", pareja.getUsuario().getNombre(), pareja.getEstaVivo(), pareja.getMuerteConfirmada());
                            pareja.setEstaVivo(false);
                            pareja.setMuerteConfirmada(true);
                            salaUsuarioRepository.save(pareja);
                            
                            String nombrePareja = pareja.getUsuario().getNombre();
                            String rolPareja = pareja.getRol() != null ? pareja.getRol().getNombre() : " aldeano";
                            String bandoPareja = pareja.getRol() != null ? pareja.getRol().getBando().name() : "aldea";
                            
                            log.info("El enamorado {} muere junto a su pareja {} en sala {}", nombreEliminado, nombrePareja, codigoSala);
                            partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(nombrePareja, rolPareja, bandoPareja, true, MuerteConfirmadaDto.TIPO_CONFIRMAR));
                        });
                    }
                } else {
                    log.info("{} ya tiene muerte confirmada, saltando", su.getUsuario().getNombre());
                }
            }
        }

        sala.setEstadoDia(nuevaFase);
        salaRepository.save(sala);

        log.info("Fase cambiada a {} en sala {} (ronda {})", nuevaFase, codigoSala, sala.getRondaActual());
        partidaSocketService.notificarCambioFase(codigoSala, nuevaFase);
    }

    // -------------------------------------------------------
    // Abrir sesión de votación
    // -------------------------------------------------------
    @Transactional
    public SesionVotacion abrirVotacion(String codigoSala, AbrirVotacionRequest request) {
        Usuario solicitante = getUsuarioAutenticado();
        log.info("Narrador {} abriendo votacion tipo {} en sala {}", solicitante.getNombre(), request.tipo(), codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            log.warn("Usuario {} tentou abrir votacion sin ser narrador", solicitante.getNombre());
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        if (sesionVotacionRepository.existsBySala_IdSalaAndAbiertaTrue(sala.getIdSala())) {
            log.warn("Ya existe votacion abierta en sala {}", codigoSala);
            throw new IllegalStateException(VOTACION_YA_ABIERTA);
        }

        SesionVotacion sesion = SesionVotacion.builder().sala(sala).tipo(request.tipo()).ronda(sala.getRondaActual()).build();

        sesionVotacionRepository.save(sesion);

        log.info("Votacion {} abierta en sala {} (sesion id: {})", request.tipo(), codigoSala, sesion.getIdSesion());
        partidaSocketService.notificarVotacion(codigoSala, sesion.getIdSesion(), sesion.getTipo().name(), true);

        return sesion;
    }

    // -------------------------------------------------------
    // Cerrar sesión de votación y calcular resultado
    // -------------------------------------------------------
    @Transactional
    public ResultadoVotacionDto cerrarVotacion(String codigoSala, Integer idSesion) {
        Usuario solicitante = getUsuarioAutenticado();
        log.info("Narrador {} cerrando votacion {} en sala {}", solicitante.getNombre(), idSesion, codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        SesionVotacion sesion = sesionVotacionRepository.findById(idSesion).orElseThrow(() -> new IllegalArgumentException("Sesión de votación no encontrada"));

        if (!sesion.getAbierta()) {
            log.warn("Sesion {} ya estaba cerrada", idSesion);
            throw new IllegalStateException("La sesión ya está cerrada");
        }

        sesion.setAbierta(false);
        sesion.setFechaCierre(LocalDateTime.now());
        sesionVotacionRepository.save(sesion);

        ResultadoVotacionDto resultado = calcularResultado(sesion);
        log.info("Resultado votacion {}: ganador={}, eliminado={}, empate={}", 
                idSesion, resultado.nombreGanador(), resultado.nombreEliminado(), resultado.empate());

        // Elección de alcalde: asignar ganador como nuevo alcalde
        if (sesion.getTipo() == SesionVotacion.TipoVotacion.ALCALDE && !resultado.empate() && resultado.nombreGanador() != null) {

            usuarioRepository.findByNombre(resultado.nombreGanador()).ifPresent(nuevoAlcalde -> {
                sala.setAlcalde(nuevoAlcalde);
                salaRepository.save(sala);
            });

            salaSocketService.notificarAlcalde(codigoSala, resultado.nombreGanador());
        }

if (sesion.getTipo() == SesionVotacion.TipoVotacion.DIA && !resultado.empate() && resultado.nombreEliminado() != null) {
            usuarioRepository.findByNombre(resultado.nombreEliminado())
                    .flatMap(eliminado -> salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(
                            sala.getIdSala(), eliminado.getIdUsuario()))
 .ifPresent(su -> {
                        su.setEstaVivo(false);
                        su.setMuerteConfirmada(true);
                        salaUsuarioRepository.save(su);

                        if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(su.getUsuario().getIdUsuario())) {
                            sala.setAlcalde(null);
                            salaRepository.save(sala);
                            salaSocketService.notificarAlcalde(codigoSala, null);
                        }

                        String nombreEliminado = su.getUsuario().getNombre();
                        String nombreRol = su.getRol() != null ? su.getRol().getNombre() : " aldeano";
                        String bando = su.getRol() != null ? su.getRol().getBando().name() : "aldea";
                        partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(nombreEliminado, nombreRol, bando, true, MuerteConfirmadaDto.TIPO_LINCHAMIENTO));

                        Optional<Enamorados> enamoradoOpt = enamoradosRepository.findByIdSala(sala.getIdSala());
                        if (enamoradoOpt.isPresent()) {
                            Enamorados enamorado = enamoradoOpt.get();
                            Integer idJugador1 = enamorado.getUsuario1().getIdUsuario();
                            Integer idJugador2 = enamorado.getUsuario2().getIdUsuario();

                            if (su.getUsuario().getIdUsuario().equals(idJugador1) || su.getUsuario().getIdUsuario().equals(idJugador2)) {
                                Integer idPareja = su.getUsuario().getIdUsuario().equals(idJugador1) ? idJugador2 : idJugador1;
                                salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idPareja)
                                        .filter(p -> p.getEstaVivo() != null && p.getEstaVivo())
                                        .ifPresent(pareja -> {
                                            pareja.setEstaVivo(false);
                                            pareja.setMuerteConfirmada(true);
                                            salaUsuarioRepository.save(pareja);

                                            if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(pareja.getUsuario().getIdUsuario())) {
                                                sala.setAlcalde(null);
                                                salaRepository.save(sala);
                                                salaSocketService.notificarAlcalde(codigoSala, null);
                                            }

                                            String nombrePareja = pareja.getUsuario().getNombre();
                                            String rolPareja = pareja.getRol() != null ? pareja.getRol().getNombre() : " aldeano";
                                            String bandoPareja = pareja.getRol() != null ? pareja.getRol().getBando().name() : "aldea";
                                            log.info("El enamorado {} linchado, su pareja {} también muere en sala {}", nombreEliminado, nombrePareja, codigoSala);
                                            partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(nombrePareja, rolPareja, bandoPareja, true, MuerteConfirmadaDto.TIPO_LINCHAMIENTO));
                                        });
                            }
                        }
                    });
        }
        if (sesion.getTipo() == SesionVotacion.TipoVotacion.LOBOS
                && !resultado.empate()
                && resultado.nombreEliminado() != null) {

            usuarioRepository.findByNombre(resultado.nombreEliminado())
                    .flatMap(victima -> salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(
                            sala.getIdSala(), victima.getIdUsuario()))
                    .ifPresent(su -> {
                        su.setEstaVivo(false);
                        su.setMuerteConfirmada(false);
                        salaUsuarioRepository.save(su);

                        if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(su.getUsuario().getIdUsuario())) {
                            sala.setAlcalde(null);
                            salaRepository.save(sala);
                            salaSocketService.notificarAlcalde(codigoSala, null);
                        }
                        
                        String nombreEliminado = su.getUsuario().getNombre();
                        String nombreRol = su.getRol() != null ? su.getRol().getNombre() : " aldeano";
                        String bando = su.getRol() != null ? su.getRol().getBando().name() : "aldea";
                        partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(nombreEliminado, nombreRol, bando, false, MuerteConfirmadaDto.TIPO_MUERTE));
                    });
        }

        partidaSocketService.notificarVotacion(codigoSala, sesion.getIdSesion(), sesion.getTipo().name(), false);
        partidaSocketService.notificarResultadoVotacion(codigoSala, resultado);

        comprobarFinPartida(codigoSala, sala);

        return resultado;
    }

    // -------------------------------------------------------
    // Emitir o cambiar voto (upsert)
    // -------------------------------------------------------
    @Transactional
    public void votar(String codigoSala, VotarRequest request) {
        Usuario votante = getUsuarioAutenticado();
        log.debug("Usuario {} votando en sala {}", votante.getNombre(), codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        SesionVotacion sesion = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).orElseThrow(() -> new IllegalStateException(VOTACION_NO_ENCONTRADA));

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), votante.getIdUsuario()).orElseThrow(() -> new IllegalStateException(JUGADOR_NO_EN_SALA));

        if (!salaUsuario.getEstaVivo()) {
            log.warn("Jugador {} intentado votar pero esta eliminado", votante.getNombre());
            throw new IllegalStateException(JUGADOR_ELIMINADO);
        }

        Usuario objetivo = usuarioRepository.findById(request.idObjetivo()).orElseThrow(() -> new IllegalArgumentException("Jugador objetivo no encontrado"));

        SalaUsuario objetivoEnSala = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivo.getIdUsuario()).orElseThrow(() -> new IllegalArgumentException("El objetivo no está en esta sala"));

        if (!objetivoEnSala.getEstaVivo()) {
            log.warn("Usuario {} intentó votar a {} que ya esta eliminado", votante.getNombre(), objetivo.getNombre());
            throw new IllegalStateException("No puedes votar a un jugador eliminado");
        }

        if (sesion.getTipo() == SesionVotacion.TipoVotacion.LOBOS) {
            if (salaUsuario.getRol() != null && salaUsuario.getRol().getBando() == Rol.Bando.lobo
                    && objetivoEnSala.getRol() != null && objetivoEnSala.getRol().getBando() == Rol.Bando.lobo) {
                log.warn("Lobo {} intentó votar a otro lobo {}", votante.getNombre(), objetivo.getNombre());
                throw new IllegalStateException("No puedes votar a otro lobo");
            }
        }

        Optional<Voto> votoExistente = votoRepository.findBySesion_IdSesionAndVotante_IdUsuario(sesion.getIdSesion(), votante.getIdUsuario());

        Voto voto;
        if (votoExistente.isPresent()) {
            voto = votoExistente.get();
            voto.setObjetivo(objetivo);
            voto.setFechaVoto(LocalDateTime.now());
            log.debug("Actualizando voto de {} a {}", votante.getNombre(), objetivo.getNombre());
        } else {
            voto = Voto.builder().sesion(sesion).votante(votante).objetivo(objetivo).build();
            log.debug("Nuevo voto de {} hacia {}", votante.getNombre(), objetivo.getNombre());
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
        log.info("Narrador {} confirmando muerte de usuario {} en sala {}", solicitante.getNombre(), idUsuario, codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuarioWithLock(sala.getIdSala(), idUsuario).orElseThrow(() -> new IllegalArgumentException("El jugador no está en esta sala"));

        if (salaUsuario.getMuerteConfirmada()) {
            log.info("Muerte de {} ya estaba confirmada, no hay nada que hacer", idUsuario);
            return;
        }

        salaUsuario.setEstaVivo(false);
        salaUsuario.setMuerteConfirmada(true);
        salaUsuarioRepository.save(salaUsuario);

        log.info("Jugador {} marcado como muerto (rol: {}, bando: {})", 
                salaUsuario.getUsuario().getNombre(), 
                salaUsuario.getRol() != null ? salaUsuario.getRol().getNombre() : "sin rol",
                salaUsuario.getRol() != null ? salaUsuario.getRol().getBando().name() : "unknown");

        if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(idUsuario)) {
            sala.setAlcalde(null);
            salaRepository.save(sala);
            salaSocketService.notificarAlcalde(codigoSala, null);
        }

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(salaUsuario.getUsuario().getNombre(), salaUsuario.getRol().getNombre(), salaUsuario.getRol().getBando().name(), true, MuerteConfirmadaDto.TIPO_CONFIRMAR);

        partidaSocketService.notificarMuerte(codigoSala, muerte);

        // Si el jugador muerto es el CAZADOR, el narrador decidirá cuándo activar su poder (solo durante el día)
        if (ROL_CAZADOR.equals(salaUsuario.getRol().getNombre())) {
            log.info("El Cazador {} ha muerto. El narrador podrá activar su poder durante la fase de día.", salaUsuario.getUsuario().getNombre());
            // El narrador activará manualmente usando el endpoint de habilidad cuando sea de día
        }

        // Si el jugador muerto está ENAMORADO, marcar al otro como semi-muerto (pendiente de confirmar)
        Optional<Enamorados> enamoradoOpt = enamoradosRepository.findByIdSala(sala.getIdSala());
        if (enamoradoOpt.isPresent()) {
            Enamorados enamorado = enamoradoOpt.get();
            Integer idJugador1 = enamorado.getUsuario1().getIdUsuario();
            Integer idJugador2 = enamorado.getUsuario2().getIdUsuario();

            if (idUsuario.equals(idJugador1) || idUsuario.equals(idJugador2)) {
                Integer idPareja = idUsuario.equals(idJugador1) ? idJugador2 : idJugador1;
                Optional<SalaUsuario> parejaOpt = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idPareja);

                if (parejaOpt.isPresent() && parejaOpt.get().getEstaVivo()) {
                    SalaUsuario pareja = parejaOpt.get();
                    log.info("El enamorado {} ha muerto, su pareja {} queda viva pero morirá si la muerte se confirma", salaUsuario.getUsuario().getNombre(), pareja.getUsuario().getNombre());
                    // El enamorado 2 sigue vivo, morirá solo si la muerte del enamorado 1 se confirma al pasar de día
                }
            }
        }

        // Si el jugador confirmado muerto es el MENTOR del Niño Salvaje, transformarlo
        List<SalaUsuario> ninosConMentor = salaUsuarioRepository.findBySala_IdSalaAndMentor_IdUsuario(sala.getIdSala(), idUsuario);
        for (SalaUsuario nino : ninosConMentor) {
            if (ROL_NINO_SALVAJE.equals(nino.getRol().getNombre())) {
                rolRepository.findByNombre(ROL_NINO_LOBO).ifPresent(rolLobo -> {
                    nino.setRol(rolLobo);
                    salaUsuarioRepository.save(nino);

                    salaSocketService.enviarRolPrivado(nino.getUsuario().getNombre(), new SalaSocketService.RolAsignadoEvent(WS_EVENTO_ROL_CAMBIADO, rolLobo.getIdRol(), rolLobo.getNombre(), rolLobo.getDescripcion(), rolLobo.getBando().name()));

                    List<String> companerosLobos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream().filter(su -> su.getEstaVivo() && su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo && !su.getUsuario().getIdUsuario().equals(nino.getUsuario().getIdUsuario())).map(su -> su.getUsuario().getNombre()).toList();

                    salaSocketService.enviarMensajePrivado(nino.getUsuario().getNombre(), "/queue/lobos", new SalaSocketService.CompañerosLobosEvent(WS_EVENTO_COMPANEROS_LOBOS, companerosLobos));
                });
            }
        }

        comprobarFinPartida(codigoSala, sala);
    }

    // -------------------------------------------------------
    // Comprobar si la partida ha terminado
    // -------------------------------------------------------
    private void comprobarFinPartida(String codigoSala, Sala sala) {
        List<SalaUsuario> jugadoresVivos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream().filter(su -> su.getEstaVivo() && !su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario())).toList();

        if (jugadoresVivos.isEmpty()) {
            sala.setEstadoSala(Sala.EstadoSala.CERRADA);
            salaRepository.save(sala);
            partidaSocketService.notificarFinPartida(codigoSala, new FinPartidaDto(null, "¡La partida ha terminado en tablas!"));
            return;
        }

        long lobosVivos = jugadoresVivos.stream().filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo).count();

        long aldeaViva = jugadoresVivos.stream().filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.aldea).count();

        // Verificar si hay enamorado y si son los únicos vivos (bando secreto)
        Optional<Enamorados> enamoradoOpt = enamoradosRepository.findByIdSala(sala.getIdSala());
        boolean enamoradoSonUltimos = false;
        if (enamoradoOpt.isPresent() && jugadoresVivos.size() == 2) {
            Enamorados enamorado = enamoradoOpt.get();
            Integer idEnamorado1 = enamorado.getUsuario1().getIdUsuario();
            Integer idEnamorado2 = enamorado.getUsuario2().getIdUsuario();

            boolean enamorado1Vivo = jugadoresVivos.stream().anyMatch(su -> su.getUsuario().getIdUsuario().equals(idEnamorado1));
            boolean enamorado2Vivo = jugadoresVivos.stream().anyMatch(su -> su.getUsuario().getIdUsuario().equals(idEnamorado2));

            if (enamorado1Vivo && enamorado2Vivo) {
                enamoradoSonUltimos = true;
            }
        }

        String bandoGanador = null;
        String mensaje = null;

        // Si los enamorado son los últimos dos, ganan como bando secreto
        if (enamoradoSonUltimos) {
            bandoGanador = BANDO_ENAMORADOS;
            mensaje = "¡Los amantes han ganado! Eliminaron a todos los demás jugadores.";
        } else if (aldeaViva > 0 && lobosVivos == 0) {
            bandoGanador = BANDO_ALDEA;
            mensaje = "¡La aldea ha eliminado a todos los hombres lobo!";
        } else if (lobosVivos > 0 && aldeaViva == 0) {
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
        log.info("Narrador {} cerrando partida en sala {}", solicitante.getNombre(), codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(ErrorMessages.SOLO_NARRADOR);
        }

        sala.setEstadoSala(Sala.EstadoSala.CERRADA);
        salaRepository.save(sala);

        log.info("Partida {} cerrada por narrador", codigoSala);
        partidaSocketService.notificarFinPartida(codigoSala, new FinPartidaDto(null, "Partida cerrada por el narrador"));
    }

    @Transactional
    public void cancelarPartida(String codigoSala) {
        Usuario solicitante = getUsuarioAutenticado();
        log.info("Narrador {} cancelando partida (empate) en sala {}", solicitante.getNombre(), codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(ErrorMessages.SOLO_NARRADOR);
        }

        sala.setEstadoSala(Sala.EstadoSala.CERRADA);
        salaRepository.save(sala);

        log.info("Partida {} cancelada por narrador - EMPATE", codigoSala);
        partidaSocketService.notificarFinPartida(codigoSala, new FinPartidaDto("empate", "El narrador ha cancelado la partida. ¡Empate!"));
    }

    @Transactional
    public void rendirse(String codigoSala) {
        Usuario jugador = getUsuarioAutenticado();
        log.info("Jugador {} rindiéndose en sala {}", jugador.getNombre(), codigoSala);

        Sala sala = getSalaIniciada(codigoSala);

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), jugador.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.JUGADOR_NO_EN_SALA));

        if (!salaUsuario.getEstaVivo()) {
            log.warn("Jugador {} intentó rendirse pero ya está eliminado", jugador.getNombre());
            throw new IllegalStateException("Ya estás eliminado");
        }

        salaUsuario.setEstaVivo(false);
        salaUsuario.setMuerteConfirmada(true);
        salaUsuarioRepository.save(salaUsuario);

        String nombreRol = salaUsuario.getRol() != null ? salaUsuario.getRol().getNombre() : " aldeano";
        String bando = salaUsuario.getRol() != null ? salaUsuario.getRol().getBando().name() : "aldea";
        partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(jugador.getNombre(), nombreRol, bando, true, MuerteConfirmadaDto.TIPO_RENDIRSE));

        if (sala.getAlcalde() != null && sala.getAlcalde().getIdUsuario().equals(jugador.getIdUsuario())) {
            sala.setAlcalde(null);
            salaRepository.save(sala);
            salaSocketService.notificarAlcalde(codigoSala, null);
        }

        if (salaUsuario.getMentor() != null) {
            List<SalaUsuario> ninosConMentor = salaUsuarioRepository.findBySala_IdSalaAndMentor_IdUsuario(sala.getIdSala(), jugador.getIdUsuario());
            for (SalaUsuario nino : ninosConMentor) {
                if (ROL_NINO_SALVAJE.equals(nino.getRol().getNombre())) {
                    rolRepository.findByNombre(ROL_NINO_LOBO).ifPresent(rolLobo -> {
                        nino.setRol(rolLobo);
                        salaUsuarioRepository.save(nino);
                        salaSocketService.enviarRolPrivado(nino.getUsuario().getNombre(), new SalaSocketService.RolAsignadoEvent(WS_EVENTO_ROL_CAMBIADO, rolLobo.getIdRol(), rolLobo.getNombre(), rolLobo.getDescripcion(), rolLobo.getBando().name()));
                        List<String> companerosLobos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream().filter(su -> su.getEstaVivo() && su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo && !su.getUsuario().getIdUsuario().equals(nino.getUsuario().getIdUsuario())).map(su -> su.getUsuario().getNombre()).toList();
                        salaSocketService.enviarMensajePrivado(nino.getUsuario().getNombre(), "/queue/lobos", new SalaSocketService.CompañerosLobosEvent(WS_EVENTO_COMPANEROS_LOBOS, companerosLobos));
                    });
                }
            }
        }

        Optional<Enamorados> enamoradoOpt = enamoradosRepository.findByIdSala(sala.getIdSala());
        if (enamoradoOpt.isPresent()) {
            Enamorados enamorado = enamoradoOpt.get();
            Integer idJugador1 = enamorado.getUsuario1().getIdUsuario();
            Integer idJugador2 = enamorado.getUsuario2().getIdUsuario();

            if (jugador.getIdUsuario().equals(idJugador1) || jugador.getIdUsuario().equals(idJugador2)) {
                Integer idPareja = jugador.getIdUsuario().equals(idJugador1) ? idJugador2 : idJugador1;
                Optional<SalaUsuario> parejaOpt = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idPareja);
                if (parejaOpt.isPresent() && parejaOpt.get().getEstaVivo()) {
                    SalaUsuario pareja = parejaOpt.get();
                    pareja.setEstaVivo(false);
                    pareja.setMuerteConfirmada(true);
                    salaUsuarioRepository.save(pareja);
                    MuerteConfirmadaDto muertePareja = new MuerteConfirmadaDto(pareja.getUsuario().getNombre(), pareja.getRol().getNombre(), pareja.getRol().getBando().name(), true, MuerteConfirmadaDto.TIPO_RENDIRSE);
                    partidaSocketService.notificarMuerte(codigoSala, muertePareja);
                }
            }
        }

        comprobarFinPartida(codigoSala, sala);
    }

    public EstadoPartidaDto getEstadoPartida(String codigoSala) {
        log.debug("Obteniendo estado de partida para sala {}", codigoSala);
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SALA_NO_ENCONTRADA));

        if (sala.getEstadoSala() != Sala.EstadoSala.INICIADA) {
            throw new IllegalStateException(ErrorMessages.PARTIDA_NO_INICIADA);
        }

        List<JugadorDto> jugadores = jugadorService.getJugadores(codigoSala);

        SesionActivaDto sesionActiva = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala()).map(s -> new SesionActivaDto(s.getIdSesion(), s.getTipo().name(), s.getRonda(), s.getFechaInicio())).orElse(null);

        String nombreAlcalde = sala.getAlcalde() != null ? sala.getAlcalde().getNombre() : null;

        return new EstadoPartidaDto(sala.getEstadoDia().name(), sala.getRondaActual(), jugadores, sesionActiva, nombreAlcalde);
    }

    @Override
    public List<String> getNombresLobos(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SALA_NO_ENCONTRADA));
        
        List<SalaUsuario> lobos = salaUsuarioRepository.findBySala_IdSalaAndEstaVivoTrue(sala.getIdSala()).stream()
            .filter(su -> {
                String nombreRol = su.getRol() != null ? su.getRol().getNombre() : "";
                return "Lobo".equals(nombreRol) || "NIÑO LOBO".equals(nombreRol);
            })
            .toList();
        
        return lobos.stream().map(su -> su.getUsuario().getNombre()).toList();
    }

    @Override
    public void actualizarRol(String codigoSala, Integer idUsuario, String nombreRol) {
        Usuario solicitante = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SALA_NO_ENCONTRADA));

        if (sala.getNarrador() == null || !sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException("Solo el narrador puede actualizar roles");
        }
        
        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));
        
        Rol rol = rolRepository.findByNombre(nombreRol)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        
        salaUsuario.setRol(rol);
        salaUsuarioRepository.save(salaUsuario);

        partidaSocketService.notificarRolCambiado(codigoSala,
            new RolCambiadoDto(WS_EVENTO_ROL_CAMBIADO,
                salaUsuario.getUsuario().getNombre(),
                nombreRol,
                rol.getBando().name()));

        log.info("Rol de {} actualizado a {} en sala {}", salaUsuario.getUsuario().getNombre(), nombreRol, codigoSala);
    }
}
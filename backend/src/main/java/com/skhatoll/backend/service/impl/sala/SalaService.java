package com.skhatoll.backend.service.impl.sala;

import com.skhatoll.backend.dto.sala.*;
import com.skhatoll.backend.entities.*;
import com.skhatoll.backend.repository.*;
import com.skhatoll.backend.service.interfaces.jugador.IJugadorService;
import com.skhatoll.backend.service.interfaces.sala.ISalaService;
import com.skhatoll.backend.util.constants.ErrorMessages;
import com.skhatoll.backend.util.constants.GameConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.skhatoll.backend.util.constants.ErrorMessages.*;
import static com.skhatoll.backend.util.constants.GameConstants.*;

@Service
@RequiredArgsConstructor
public class SalaService implements ISalaService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SalaSocketService salaSocketService;
    private final IJugadorService jugadorService;
    private final HabilidadSalaRepository habilidadSalaRepository;

    // -------------------------------------------------------
    // Obtener el usuario autenticado desde el contexto de Security
    // -------------------------------------------------------
    private Usuario getUsuarioAutenticado() {
        String nombre = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    // -------------------------------------------------------
    // Crear sala
    // -------------------------------------------------------
    @Transactional
    public CrearSalaResponse crearSala() {
        Usuario creador = getUsuarioAutenticado();

        String codigo = generarCodigoUnico();

        Sala sala = Sala.builder()
                .narrador(creador)
                .codigoSala(codigo)
                .build();

        salaRepository.save(sala);

        SalaUsuario salaUsuario = SalaUsuario.builder()
                .sala(sala)
                .usuario(creador)
                .build();

        salaUsuarioRepository.save(salaUsuario);

        return new CrearSalaResponse(codigo);
    }

    // -------------------------------------------------------
    // Unirse a una sala
    // -------------------------------------------------------
    @Transactional
    public void unirse(UnirseRequest request) {
        Usuario usuario = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(request.getCodigoSala())
                .orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));

        if (sala.getEstadoSala() != Sala.EstadoSala.CREADA) {
            throw new IllegalStateException("La sala no está disponible para unirse");
        }

        int jugadoresActuales = salaUsuarioRepository.countBySala_IdSala(sala.getIdSala());
        if (jugadoresActuales >= sala.getMaxJugadores()) {
            throw new IllegalStateException("La sala está llena");
        }

        boolean yaEsta = salaUsuarioRepository.existsBySala_IdSalaAndUsuario_IdUsuario(
                sala.getIdSala(), usuario.getIdUsuario());
        if (yaEsta) {
            throw new IllegalStateException("Ya estás en esta sala");
        }

        SalaUsuario salaUsuario = SalaUsuario.builder()
                .sala(sala)
                .usuario(usuario)
                .build();

        salaUsuarioRepository.save(salaUsuario);

        // Notificar a todos en el lobby que hay un nuevo jugador
        List<JugadorDto> jugadoresActualizados = jugadorService.getJugadores(request.getCodigoSala());
        salaSocketService.notificarNuevoJugador(request.getCodigoSala(), jugadoresActualizados);
    }

    // -------------------------------------------------------
    // Asignar narrador
    // -------------------------------------------------------
    @Transactional
    public void asignarNarrador(String codigoSala, AsignarNarradorRequest request) {
        Usuario solicitante = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException("Solo el creador puede asignar el narrador");
        }

        boolean estaEnSala = salaUsuarioRepository.existsBySala_IdSalaAndUsuario_IdUsuario(
                sala.getIdSala(), request.getIdUsuario());
        if (!estaEnSala) {
            throw new IllegalArgumentException("El usuario no está en la sala");
        }

        Usuario nuevoNarrador = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO));

        sala.setNarrador(nuevoNarrador);
        salaRepository.save(sala);
    }

    // -------------------------------------------------------
    // Iniciar partida: asigna roles y notifica por WebSocket
    // -------------------------------------------------------
    @Transactional
    public void iniciarPartida(String codigoSala) {
        Usuario solicitante = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        List<SalaUsuario> jugadores = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala());

        List<SalaUsuario> jugadoresSinNarrador = jugadores.stream()
                .filter(su -> !su.getUsuario().getIdUsuario()
                        .equals(sala.getNarrador().getIdUsuario()))
                .toList();

        int totalJugadores = jugadoresSinNarrador.size();

        if (totalJugadores < sala.getMinJugadores()) {
            throw new IllegalStateException(
                    "Se necesitan al menos " + sala.getMinJugadores() + " jugadores para iniciar");
        }

        int numLobos = Math.max(1, totalJugadores / 4);

        Rol rolLobo = rolRepository.findByNombre(ROL_LOBO)
                .orElseThrow(() -> new IllegalStateException("Rol Lobo no encontrado en BD"));

        List<Rol> rolesAldea = rolRepository.findByBando(Rol.Bando.aldea);

        Rol rolAldeano = rolRepository.findByNombre(ROL_ALDEANO)
                .orElseThrow(() -> new IllegalStateException("Rol Aldeano no encontrado en BD"));

        rolesAldea = rolesAldea.stream()
                .filter(rol -> !rol.getNombre().equals(ROL_ALDEANO))
                .collect(Collectors.toList());

        Collections.shuffle(rolesAldea);

        int numRolesAldea = totalJugadores - numLobos;

        List<Rol> rolesARepartir = new ArrayList<>();

        for (int i = 0; i < Math.min(numRolesAldea, rolesAldea.size()); i++) {
            rolesARepartir.add(rolesAldea.get(i));
        }
        while (rolesARepartir.size() < numRolesAldea) {
            rolesARepartir.add(rolAldeano);
        }
        for (int i = 0; i < numLobos; i++) {
            rolesARepartir.add(rolLobo);
        }

        Collections.shuffle(rolesARepartir);

        for (int i = 0; i < jugadoresSinNarrador.size(); i++) {
            SalaUsuario su = jugadoresSinNarrador.get(i);
            Rol rol = rolesARepartir.get(i);

            su.setRol(rol);
            salaUsuarioRepository.save(su);

            asignarHabilidades(su, rol);

            salaSocketService.enviarRolPrivado(
                    su.getUsuario().getNombre(),
                    new SalaSocketService.RolAsignadoEvent(
                            WS_EVENTO_ROL_ASIGNADO,
                            rol.getIdRol(),
                            rol.getNombre(),
                            rol.getDescripcion(),
                            rol.getBando().name()));
        }

        sala.setEstadoSala(Sala.EstadoSala.INICIADA);
        salaRepository.save(sala);

        salaSocketService.notificarInicio(codigoSala);
    }

    // -------------------------------------------------------
    // Generar código de sala único de 6 caracteres
    // -------------------------------------------------------
    private String generarCodigoUnico() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String codigo;

        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
            }
            codigo = sb.toString();
        } while (salaRepository.existsByCodigoSala(codigo));

        return codigo;
    }

    public List<JugadorRolDto> getJugadoresConRol(String codigoSala) {
        Usuario solicitante = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException(SOLO_NARRADOR);
        }

        return salaUsuarioRepository.findBySala_IdSala(sala.getIdSala())
                .stream()
                .filter(su -> !su.getUsuario().getIdUsuario()
                        .equals(sala.getNarrador().getIdUsuario()))
                .map(su -> new JugadorRolDto(
                        su.getUsuario().getIdUsuario(),
                        su.getUsuario().getNombre(),
                        su.getUsuario().getCodigoUuid(),
                        su.getEstaVivo(),
                        su.getRol() != null ? su.getRol().getNombre() : ROL_SIN_ROL,
                        su.getRol() != null ? su.getRol().getBando().name() : ""))
                .toList();
    }

    @Transactional
    public void salirDeSala(String codigoSala) {
        Usuario usuario = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SALA_NO_ENCONTRADA));

        if (sala.getEstadoSala() != Sala.EstadoSala.CREADA) {
            throw new IllegalStateException("No puedes salir de una partida que ya ha comenzado");
        }

        if (sala.getNarrador().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalStateException("El narrador no puede abandonar la sala");
        }

        SalaUsuario salaUsuario = salaUsuarioRepository
                .findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.JUGADOR_NO_EN_SALA));

        salaUsuarioRepository.delete(salaUsuario);

        List<JugadorDto> jugadoresActualizados = jugadorService.getJugadores(codigoSala);
        salaSocketService.notificarJugadorSalio(codigoSala, jugadoresActualizados);
    }

    private void asignarHabilidades(SalaUsuario salaUsuario, Rol rol) {
        List<String> habilidades = switch (rol.getNombre()) {
            case GameConstants.ROL_BRUJA -> List.of(
                    GameConstants.HAB_POCION_VIDA,
                    GameConstants.HAB_POCION_MUERTE);
            case GameConstants.ROL_CAZADOR -> List.of(
                    GameConstants.HAB_DISPARO);
            case GameConstants.ROL_CUPIDO -> List.of(
                    GameConstants.HAB_FLECHAZO);
            case GameConstants.ROL_NINO_SALVAJE -> List.of(
                    GameConstants.HAB_MODELO);
            default -> List.of();
        };

        habilidades.forEach(nombre -> {
            HabilidadSala habilidad = HabilidadSala.builder()
                    .idSalaUsuario(salaUsuario)
                    .nombre(nombre)
                    .build();
            habilidadSalaRepository.save(habilidad);
        });
    }
}

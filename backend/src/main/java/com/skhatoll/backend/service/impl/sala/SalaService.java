package com.skhatoll.backend.service.impl.sala;

import com.skhatoll.backend.dto.sala.*;
import com.skhatoll.backend.entities.Rol;
import com.skhatoll.backend.entities.Sala;
import com.skhatoll.backend.entities.SalaUsuario;
import com.skhatoll.backend.repository.RolRepository;
import com.skhatoll.backend.repository.SalaRepository;
import com.skhatoll.backend.repository.SalaUsuarioRepository;
import com.skhatoll.backend.entities.Usuario;
import com.skhatoll.backend.repository.UsuarioRepository;
import com.skhatoll.backend.service.interfaces.sala.ISalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaService implements ISalaService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SalaSocketService salaSocketService;

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
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

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
        List<JugadorDto> jugadoresActualizados = getJugadores(request.getCodigoSala());
        salaSocketService.notificarNuevoJugador(request.getCodigoSala(), jugadoresActualizados);
    }

    // -------------------------------------------------------
    // Obtener lista de jugadores de una sala
    // -------------------------------------------------------
    public List<JugadorDto> getJugadores(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        return salaUsuarioRepository.findBySala_IdSala(sala.getIdSala())
                .stream()
                .map(su -> new JugadorDto(
                        su.getUsuario().getIdUsuario(),
                        su.getUsuario().getNombre(),
                        su.getUsuario().getCodigoUuid(),
                        su.getEstaVivo(),
                        su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario()),
                        sala.getAlcalde() != null && su.getUsuario().getIdUsuario().equals(sala.getAlcalde().getIdUsuario())))
                .toList();
    }

    // -------------------------------------------------------
    // Asignar narrador
    // -------------------------------------------------------
    @Transactional
    public void asignarNarrador(String codigoSala, AsignarNarradorRequest request) {
        Usuario solicitante = getUsuarioAutenticado();

        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException("Solo el creador puede asignar el narrador");
        }

        boolean estaEnSala = salaUsuarioRepository.existsBySala_IdSalaAndUsuario_IdUsuario(
                sala.getIdSala(), request.getIdUsuario());
        if (!estaEnSala) {
            throw new IllegalArgumentException("El usuario no está en la sala");
        }

        Usuario nuevoNarrador = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

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
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException("Solo el narrador puede iniciar la partida");
        }

        List<SalaUsuario> jugadores = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala());

        // Excluir al narrador de la asignación de roles
        List<SalaUsuario> jugadoresSinNarrador = jugadores.stream()
                .filter(su -> !su.getUsuario().getIdUsuario()
                        .equals(sala.getNarrador().getIdUsuario()))
                .toList();

        int totalJugadores = jugadoresSinNarrador.size();

        if (totalJugadores < sala.getMinJugadores()) {
            throw new IllegalStateException(
                    "Se necesitan al menos " + sala.getMinJugadores() + " jugadores para iniciar");
        }

        // Calcular número de lobos: 1 lobo por cada 4 jugadores (mínimo 1)
        int numLobos = Math.max(1, totalJugadores / 4);

        // Obtener roles de BD
        Rol rolLobo = rolRepository.findByNombre("Lobo")
                .orElseThrow(() -> new IllegalStateException("Rol Lobo no encontrado en BD"));

        List<Rol> rolesAldea = rolRepository.findByBando(Rol.Bando.aldea);

        Rol rolAldeano = rolRepository.findByNombre("Aldeano")
                .orElseThrow(() -> new IllegalStateException("Rol Aldeano no encontrado en BD"));

        rolesAldea = rolesAldea.stream()
                .filter(rol -> !rol.getNombre().equals("Aldeano"))
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

        // Asignar roles y notificar a cada jugador por WebSocket privado
        for (int i = 0; i < jugadoresSinNarrador.size(); i++) {
            SalaUsuario su = jugadoresSinNarrador.get(i);
            Rol rol = rolesARepartir.get(i);

            su.setRol(rol);
            salaUsuarioRepository.save(su);

            salaSocketService.enviarRolPrivado(
                    su.getUsuario().getNombre(),
                    new SalaSocketService.RolAsignadoEvent(
                            "ROL_ASIGNADO",
                            rol.getIdRol(),
                            rol.getNombre(),
                            rol.getDescripcion(),
                            rol.getBando().name()));
        }

        // Cambiar estado de la sala e informar a todos
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
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        // Solo el narrador puede ver los roles
        if (!sala.getNarrador().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new IllegalStateException("Solo el narrador puede ver los roles");
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
                        su.getRol() != null ? su.getRol().getNombre() : "Sin rol",
                        su.getRol() != null ? su.getRol().getBando().name() : ""))
                .toList();
    }
}
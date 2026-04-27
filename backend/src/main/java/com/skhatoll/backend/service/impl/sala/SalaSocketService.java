package com.skhatoll.backend.service.impl.sala;

import com.skhatoll.backend.dto.sala.JugadorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalaSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    // -------------------------------------------------------
    // Notifica a todos en el lobby que un nuevo jugador se unió
    // Canal: /topic/sala/{codigo}
    // -------------------------------------------------------
    public void notificarNuevoJugador(String codigoSala, List<JugadorDto> jugadores) {
        messagingTemplate.convertAndSend(
                "/topic/sala/" + codigoSala,
                new JugadoresActualizadosEvent("JUGADOR_UNIDO", jugadores));
    }

    // -------------------------------------------------------
    // Notifica a todos que la partida ha comenzado
    // Canal: /topic/sala/{codigo}/inicio
    // -------------------------------------------------------
    public void notificarInicio(String codigoSala) {
        messagingTemplate.convertAndSend(
                "/topic/sala/" + codigoSala + "/inicio",
                new PartidaIniciadaEvent("PARTIDA_INICIADA"));
    }
//añadido 2 MAP?
    public void notificarSalaCerrada(String codigoSala) {
        messagingTemplate.convertAndSend(
                "/topic/sala/" + codigoSala + "/cerrada",
                Map.<Object, Object>of("tipo", "SALA_CERRADA")
        );
    }

    // -------------------------------------------------------
    // Envía el rol asignado de forma privada a cada jugador
    // Canal: /user/queue/rol  (solo llega al jugador concreto)
    // -------------------------------------------------------
    public void enviarRolPrivado(String nombreUsuario, RolAsignadoEvent evento) {
        messagingTemplate.convertAndSendToUser(
                nombreUsuario,
                "/queue/rol",
                evento);
    }

    // -------------------------------------------------------
    // Eventos (clases internas para serializar como JSON)
    // -------------------------------------------------------

    @Data
    @AllArgsConstructor
    public static class JugadoresActualizadosEvent {
        private String tipo;
        private List<JugadorDto> jugadores;
    }

    @Data
    @AllArgsConstructor
    public static class PartidaIniciadaEvent {
        private String tipo;
    }

    @Data
    @AllArgsConstructor
    public static class RolAsignadoEvent {
        private String tipo;
        private Integer idRol;
        private String nombreRol;
        private String descripcionRol;
        private String bando;
    }

    @Data
    @AllArgsConstructor
    public static class AlcaldeEvent {
        private String tipo;
        private String nombreAlcalde;
    }

    public void notificarAlcalde(String codigoSala, String nombreAlcalde) {
        messagingTemplate.convertAndSend(
                "/topic/partida/" + codigoSala + "/alcalde",
                new AlcaldeEvent("ALCALDE_ELEGIDO", nombreAlcalde));
    }
}

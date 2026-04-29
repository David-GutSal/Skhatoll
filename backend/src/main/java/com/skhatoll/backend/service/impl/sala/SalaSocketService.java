package com.skhatoll.backend.service.impl.sala;

import com.skhatoll.backend.dto.sala.JugadorDto;
import com.skhatoll.backend.util.constants.GameConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.skhatoll.backend.util.constants.GameConstants.*;

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
                WS_SALA,
                new JugadoresActualizadosEvent(WS_EVENTO_JUGADOR_UNIDO, jugadores));
    }

    // -------------------------------------------------------
    // Notifica a todos que la partida ha comenzado
    // Canal: /topic/sala/{codigo}/inicio
    // -------------------------------------------------------
    public void notificarInicio(String codigoSala) {
        messagingTemplate.convertAndSend(
                WS_SALA_INICIO,
                new PartidaIniciadaEvent(WS_EVENTO_PARTIDA_INICIADA));
    }

    // -------------------------------------------------------
    // Envía el rol asignado de forma privada a cada jugador
    // Canal: /user/queue/rol  (solo llega al jugador concreto)
    // -------------------------------------------------------
    public void enviarRolPrivado(String nombreUsuario, RolAsignadoEvent evento) {
        messagingTemplate.convertAndSendToUser(
                nombreUsuario,
                WS_QUEUE_ROL,
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
                WS_ALCALDE,
                new AlcaldeEvent(WS_EVENTO_ALCALDE_ELEGIDO, nombreAlcalde));
    }

    public void notificarJugadorSalio(String codigoSala, List<JugadorDto> jugadores) {
        messagingTemplate.convertAndSend(
                String.format(GameConstants.WS_SALA, codigoSala),
                new JugadoresActualizadosEvent(
                        GameConstants.WS_EVENTO_JUGADOR_SALIO, jugadores));
    }

    @Data
    @AllArgsConstructor
    public static class CompañerosLobosEvent {
        private String tipo;
        private List<String> lobos;
    }

    public void enviarMensajePrivado(String nombreUsuario, String destino, Object evento) {
        messagingTemplate.convertAndSendToUser(nombreUsuario, destino, evento);
    }
}

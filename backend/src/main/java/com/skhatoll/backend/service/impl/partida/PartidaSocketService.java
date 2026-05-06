package com.skhatoll.backend.service.impl.partida;

import com.skhatoll.backend.dto.partida.FinPartidaDto;
import com.skhatoll.backend.dto.partida.MuerteConfirmadaDto;
import com.skhatoll.backend.dto.partida.ResultadoVotacionDto;
import com.skhatoll.backend.dto.partida.VotoDto;
import com.skhatoll.backend.entities.Sala;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.skhatoll.backend.util.constants.GameConstants.*;

@Service
@RequiredArgsConstructor
public class PartidaSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    // -------------------------------------------------------
    // Notifica el cambio de fase DÍA/NOCHE
    // Canal: /topic/partida/{codigo}/fase
    // -------------------------------------------------------
    public void notificarCambioFase(String codigoSala, Sala.EstadoDia nuevaFase) {
        messagingTemplate.convertAndSend(String.format(WS_FASE, codigoSala), new FaseEvent(WS_EVENTO_CAMBIO_FASE, nuevaFase.name()));
    }

    // -------------------------------------------------------
    // Notifica apertura o cierre de votación
    // Canal: /topic/partida/{codigo}/votacion
    // -------------------------------------------------------
    public void notificarVotacion(String codigoSala, Integer idSesion, String tipo, boolean abierta) {
        messagingTemplate.convertAndSend(String.format(WS_VOTACION, codigoSala), new VotacionEvent(abierta ? WS_EVENTO_VOTACION_ABIERTA : WS_EVENTO_VOTACION_CERRADA, idSesion, tipo, abierta));
    }

    // -------------------------------------------------------
    // Notifica el resultado al cerrar la votación
    // Canal: /topic/partida/{codigo}/votacion
    // -------------------------------------------------------
    public void notificarResultadoVotacion(String codigoSala, ResultadoVotacionDto resultado) {
        messagingTemplate.convertAndSend(WS_VOTACION, resultado);
    }

    // -------------------------------------------------------
    // Notifica el estado actual de los votos en tiempo real
    // Canal: /topic/partida/{codigo}/votos
    // -------------------------------------------------------
    public void notificarVotos(String codigoSala, List<VotoDto> votos) {
        messagingTemplate.convertAndSend(String.format(WS_VOTOS, codigoSala), new VotosEvent(WS_EVENTO_VOTOS_ACTUALIZADOS, votos));
    }

    // -------------------------------------------------------
    // Notifica una muerte confirmada por el narrador
    // Canal: /topic/partida/{codigo}/muerte
    // -------------------------------------------------------
    public void notificarMuerte(String codigoSala, MuerteConfirmadaDto muerte) {
        messagingTemplate.convertAndSend(String.format(WS_MUERTE, codigoSala), muerte);
    }

    // -------------------------------------------------------
    // Notifica el fin de partida con el bando ganador
    // Canal: /topic/partida/{codigo}/fin
    // -------------------------------------------------------
    public void notificarFinPartida(String codigoSala, FinPartidaDto fin) {
        messagingTemplate.convertAndSend(String.format(WS_FIN, codigoSala), fin);
    }

    // -------------------------------------------------------
    // Eventos internos
    // -------------------------------------------------------

    @Data
    @AllArgsConstructor
    public static class FaseEvent {
        private String tipo;
        private String fase;
    }

    @Data
    @AllArgsConstructor
    public static class VotacionEvent {
        private String tipo;
        private Integer idSesion;
        private String tipoVotacion;
        private boolean abierta;
    }

    @Data
    @AllArgsConstructor
    public static class VotosEvent {
        private String tipo;
        private List<VotoDto> votos;
    }
}
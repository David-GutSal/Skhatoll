package com.skhatoll.backend.dto.partida;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoVotacionDto {
    private Integer idSesion;
    private String tipo;
    private Integer idUsuario;     // 👈 NUEVO
    private String nombreJugador;  // 👈 NUEVO
    private String nombreEliminado;
    private String nombreGanador;
    private boolean empate;
}

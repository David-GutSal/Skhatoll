package com.skhatoll.backend.dto.partida;

public record ResultadoVotacionDto(
    Integer idSesion,
    String tipo,
    String nombreEliminado,
    String nombreGanador,
    boolean empate
) {}

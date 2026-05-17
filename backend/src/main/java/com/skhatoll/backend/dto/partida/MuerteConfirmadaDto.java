package com.skhatoll.backend.dto.partida;

public record MuerteConfirmadaDto(
    String nombreJugador,
    String nombreRol,
    String bando,
    boolean muerteConfirmada,
    String tipo
) {
    public static final String TIPO_MUERTE = "MUERTE";
    public static final String TIPO_REVIVIR = "REVIVIR";
    public static final String TIPO_CONFIRMAR = "CONFIRMAR";
}

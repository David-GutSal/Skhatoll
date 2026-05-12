package com.skhatoll.backend.dto.sala;

public record JugadorDto(
    Integer idUsuario,
    String nombre,
    String codigoUuid,
    Boolean estaVivo,
    boolean esNarrador,
    Boolean esAlcalde
) {}
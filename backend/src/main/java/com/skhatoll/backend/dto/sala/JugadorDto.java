package com.skhatoll.backend.dto.sala;

public record JugadorDto(
    Integer idUsuario,
    String nombre,
    String codigoUuid,
    Boolean estaVivo,
    Boolean muerteConfirmada,
    boolean esNarrador,
    Boolean esAlcalde,
    Boolean esMentor
) {}
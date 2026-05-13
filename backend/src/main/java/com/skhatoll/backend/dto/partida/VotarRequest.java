package com.skhatoll.backend.dto.partida;

import jakarta.validation.constraints.NotNull;

public record VotarRequest(
    @NotNull(message = "El objetivo es obligatorio")
    Integer idObjetivo
) {}

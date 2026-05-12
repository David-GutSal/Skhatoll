package com.skhatoll.backend.dto.sala;

import jakarta.validation.constraints.NotBlank;

public record UnirseRequest(
    @NotBlank(message = "El codigo de sala es obligatorio")
    String codigoSala
) {}


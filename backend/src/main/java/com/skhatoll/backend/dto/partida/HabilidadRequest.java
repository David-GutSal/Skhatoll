package com.skhatoll.backend.dto.partida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HabilidadRequest(
    @NotBlank(message = "El nombre de habilidad es obligatorio")
    String nombreHabilidad,
    
    @NotNull(message = "Los objetivos son obligatorios")
    java.util.List<@NotNull Integer> objetivos
) {}

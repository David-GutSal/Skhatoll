package com.skhatoll.backend.dto.partida;

import java.util.List;

public record HabilidadResultadoDto(
    String nombreHabilidad,
    List<String> objetivos,
    String resultado,
    Object detalle
) {}

package com.skhatoll.backend.dto.partida;

import java.time.LocalDateTime;

public record SesionActivaDto(
    Integer idSesion,
    String tipo,
    Integer ronda,
    LocalDateTime fechaInicio
) {}

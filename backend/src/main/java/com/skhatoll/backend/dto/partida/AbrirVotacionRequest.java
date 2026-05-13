package com.skhatoll.backend.dto.partida;

import com.skhatoll.backend.entities.SesionVotacion;
import jakarta.validation.constraints.NotNull;

public record AbrirVotacionRequest(
    @NotNull(message = "El tipo de votacion es obligatorio")
    SesionVotacion.TipoVotacion tipo
) {}

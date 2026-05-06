package com.skhatoll.backend.dto.partida;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SesionActivaDto {
    private Integer idSesion;
    private String tipo;
    private Integer ronda;
    private LocalDateTime fechaInicio;
}

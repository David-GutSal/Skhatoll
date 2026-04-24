package com.skhatoll.backend.dto.partida;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class HabilidadResultadoDto {
    private String nombreHabilidad;
    private List<String> objetivos;
    private String resultado;
    private Object detalle;
}

package com.skhatoll.backend.dto.partida;

import lombok.Data;
import java.util.List;

@Data
public class HabilidadRequest {
    private String nombreHabilidad;
    private List<Integer> objetivos;
}

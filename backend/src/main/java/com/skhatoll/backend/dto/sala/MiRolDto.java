package com.skhatoll.backend.dto.sala;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MiRolDto {
    private String nombreRol;
    private String descripcionRol;
    private String bando;
}
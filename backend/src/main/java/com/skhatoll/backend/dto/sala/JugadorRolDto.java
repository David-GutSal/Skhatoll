package com.skhatoll.backend.dto.sala;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JugadorRolDto {
    private Integer idUsuario;
    private String nombre;
    private String codigoUuid;
    private Boolean estaVivo;
    private String nombreRol;
    private String bando;
}

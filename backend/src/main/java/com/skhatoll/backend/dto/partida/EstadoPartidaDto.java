package com.skhatoll.backend.dto.partida;

import com.skhatoll.backend.dto.sala.JugadorDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EstadoPartidaDto {
    private String estadoDia;
    private Integer rondaActual;
    private List<JugadorDto> jugadores;
    private SesionActivaDto sesionActiva;
    private String nombreAlcalde;
}

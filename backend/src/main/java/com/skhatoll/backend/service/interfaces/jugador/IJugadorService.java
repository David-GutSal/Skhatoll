package com.skhatoll.backend.service.interfaces.jugador;

import com.skhatoll.backend.dto.sala.JugadorDto;
import java.util.List;

public interface IJugadorService {
    List<JugadorDto> getJugadores(String codigoSala);
}

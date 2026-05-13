package com.skhatoll.backend.service.interfaces.partida;

import com.skhatoll.backend.dto.partida.HabilidadResultadoDto;
import com.skhatoll.backend.dto.partida.HabilidadRequest;

public interface IHabilidadService {
    HabilidadResultadoDto usarHabilidad(String codigoSala, HabilidadRequest request);
}
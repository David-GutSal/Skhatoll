package com.skhatoll.backend.service.interfaces.partida;

import com.skhatoll.backend.dto.partida.*;
import com.skhatoll.backend.entities.SesionVotacion;

public interface IPartidaService {
    void confirmarMuerte(String codigo, Integer idUsuario);

    void votar(String codigo, VotarRequest request);

    ResultadoVotacionDto cerrarVotacion(String codigo, Integer idSesion);

    SesionVotacion abrirVotacion(String codigo, AbrirVotacionRequest request);
    
    void cambiarFase(String codigo);

    SesionVotacion getSesionActiva(String codigo);

    HabilidadResultadoDto usarHabilidad(String codigo, HabilidadRequest request);

    EstadoPartidaDto getEstadoPartida(String codigo);

    void cerrarPartida(String codigo);
}

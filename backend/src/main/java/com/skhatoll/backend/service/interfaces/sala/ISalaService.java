package com.skhatoll.backend.service.interfaces.sala;

import com.skhatoll.backend.dto.sala.*;

import java.util.List;

public interface ISalaService {
    CrearSalaResponse crearSala();

    void unirse(UnirseRequest request);

    List<JugadorDto> getJugadores(String codigo);

    void asignarNarrador(String codigo, AsignarNarradorRequest request);

    void iniciarPartida(String codigo);

    List<JugadorRolDto> getJugadoresConRol(String codigo);
}

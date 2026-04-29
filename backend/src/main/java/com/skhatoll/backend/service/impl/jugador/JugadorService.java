package com.skhatoll.backend.service.impl.jugador;

import com.skhatoll.backend.dto.sala.JugadorDto;
import com.skhatoll.backend.entities.Sala;
import com.skhatoll.backend.repository.SalaRepository;
import com.skhatoll.backend.repository.SalaUsuarioRepository;
import com.skhatoll.backend.service.interfaces.jugador.IJugadorService;
import com.skhatoll.backend.util.constants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JugadorService implements IJugadorService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;

    @Override
    public List<JugadorDto> getJugadores(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SALA_NO_ENCONTRADA));

        return salaUsuarioRepository.findBySala_IdSala(sala.getIdSala())
                .stream()
                .map(su -> new JugadorDto(
                        su.getUsuario().getIdUsuario(),
                        su.getUsuario().getNombre(),
                        su.getUsuario().getCodigoUuid(),
                        su.getEstaVivo(),
                        su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario()),
                        sala.getAlcalde() != null && su.getUsuario().getIdUsuario()
                                .equals(sala.getAlcalde().getIdUsuario())))
                .toList();
    }
}
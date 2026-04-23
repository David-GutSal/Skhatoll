package com.skhatoll.backend.repository;

import com.skhatoll.backend.entities.HabilidadSala;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HabilidadSalaRepository extends JpaRepository<HabilidadSala, Integer> {
    Optional<HabilidadSala> findByIdSalaUsuario_IdSalaUsuarioAndNombre(
            Integer idSalaUsuario, String nombre);
}

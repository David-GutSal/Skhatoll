package com.skhatoll.backend.repository;

import com.skhatoll.backend.entities.Enamorados;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnamoradosRepository extends JpaRepository<Enamorados, Integer> {
    Optional<Enamorados> findByIdSala(Integer idSala);
    boolean existsByIdSala(Integer idSala);
}
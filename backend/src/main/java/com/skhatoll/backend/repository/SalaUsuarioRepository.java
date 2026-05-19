package com.skhatoll.backend.repository;

import com.skhatoll.backend.entities.SalaUsuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaUsuarioRepository extends JpaRepository<SalaUsuario, Integer> {

    List<SalaUsuario> findBySala_IdSala(Integer idSala);

    boolean existsBySala_IdSalaAndUsuario_IdUsuario(Integer idSala, Integer idUsuario);

    Optional<SalaUsuario> findBySala_IdSalaAndUsuario_IdUsuario(Integer idSala, Integer idUsuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT su FROM SalaUsuario su WHERE su.sala.idSala = :idSala AND su.usuario.idUsuario = :idUsuario")
    Optional<SalaUsuario> findBySala_IdSalaAndUsuario_IdUsuarioWithLock(Integer idSala, Integer idUsuario);

    int countBySala_IdSala(Integer idSala);

    Optional<SalaUsuario> findBySala_IdSalaAndIdModelo(Integer idSala, Integer idModelo);

    List<SalaUsuario> findBySala_IdSalaAndMentor_IdUsuario(Integer idSala, Integer idMentor);

    List<SalaUsuario> findBySala_IdSalaAndEstaVivoFalse(Integer idSala);

    List<SalaUsuario> findBySala_IdSalaAndEstaVivoTrue(Integer idSala);
}

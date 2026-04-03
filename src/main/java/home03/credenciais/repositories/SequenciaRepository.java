package home03.credenciais.repositories;

import home03.credenciais.entities.SequenciaContador;
import home03.credenciais.entities.enums.TipoSequencia;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SequenciaRepository extends JpaRepository<SequenciaContador, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SequenciaContador s WHERE s.tipo = :tipo AND s.ano = :ano")
    Optional<SequenciaContador> findByTipoAndAnoForUpdate(
            @Param("tipo") TipoSequencia tipo,
            @Param("ano") int ano);
}

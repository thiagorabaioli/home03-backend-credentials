package home03.credenciais.repositories;

import home03.credenciais.entities.HistoricoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricoEstadoRepository extends JpaRepository<HistoricoEstado, UUID> {

    List<HistoricoEstado> findByCredencialIdOrderByDataTransicaoAsc(UUID credencialId);
}

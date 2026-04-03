package home03.credenciais.repositories;

import home03.credenciais.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findAllByOrderByDataAcaoDesc(Pageable pageable);

    List<AuditLog> findByCodigoCredencialOrderByDataAcaoAsc(String codigoCredencial);

    List<AuditLog> findByUtilizadorOrderByDataAcaoDesc(String utilizador);
}

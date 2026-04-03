package home03.credenciais.repositories;

import home03.credenciais.entities.NotificacaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoEmailRepository extends JpaRepository<NotificacaoEmail, UUID> {

    List<NotificacaoEmail> findByCredencialIdOrderByDataEnvioDesc(UUID credencialId);
}

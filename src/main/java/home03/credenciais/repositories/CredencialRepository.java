package home03.credenciais.repositories;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, UUID> {

    Optional<Credencial> findByTokenValidacao(String tokenValidacao);

    Page<Credencial> findByEstado(EstadoCredencial estado, Pageable pageable);
}

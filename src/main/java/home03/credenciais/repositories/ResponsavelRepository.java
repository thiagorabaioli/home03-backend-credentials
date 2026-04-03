package home03.credenciais.repositories;

import home03.credenciais.entities.ResponsavelMercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsavelRepository extends JpaRepository<ResponsavelMercado, UUID> {

    Optional<ResponsavelMercado> findByEmail(String email);

    boolean existsByEmail(String email);
}

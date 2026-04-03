package home03.credenciais.repositories;

import home03.credenciais.entities.ColaboradorExterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColaboradorRepository extends JpaRepository<ColaboradorExterno, UUID> {

    Optional<ColaboradorExterno> findByCodigoInterno(String codigoInterno);

    boolean existsByCodigoInterno(String codigoInterno);

    Optional<ColaboradorExterno> findByEmail(String email);
}

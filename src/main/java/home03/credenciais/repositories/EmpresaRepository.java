package home03.credenciais.repositories;

import home03.credenciais.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    Optional<Empresa> findByNif(String nif);

    boolean existsByNif(String nif);

    Optional<Empresa> findByNome(String nome);
}

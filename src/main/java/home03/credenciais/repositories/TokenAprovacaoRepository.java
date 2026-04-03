package home03.credenciais.repositories;

import home03.credenciais.entities.TokenAprovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenAprovacaoRepository extends JpaRepository<TokenAprovacao, UUID> {

    Optional<TokenAprovacao> findByIdAndUtilizadoFalse(UUID id);
}

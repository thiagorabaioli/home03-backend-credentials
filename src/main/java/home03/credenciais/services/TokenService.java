package home03.credenciais.services;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.TokenAprovacao;
import home03.credenciais.repositories.TokenAprovacaoRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TokenService {

    private static final int HORAS_VALIDADE_TOKEN_RM = 72;

    private final TokenAprovacaoRepository tokenRepository;

    public TokenService(TokenAprovacaoRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public TokenAprovacao gerarTokenRM(Credencial credencial) {
        TokenAprovacao token = new TokenAprovacao(credencial, HORAS_VALIDADE_TOKEN_RM);
        return tokenRepository.save(token);
    }

    @Transactional
    public TokenAprovacao validarEConsumirToken(UUID tokenId) {
        TokenAprovacao token = tokenRepository.findByIdAndUtilizadoFalse(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido ou já utilizado"));

        if (!token.isValido()) {
            throw new BusinessException("O link de aprovação expirou. Contacte a equipa de segurança para reenvio.");
        }

        token.marcarUtilizado();
        return tokenRepository.save(token);
    }
}

package home03.credenciais.services;

import home03.credenciais.entities.SequenciaContador;
import home03.credenciais.entities.enums.TipoSequencia;
import home03.credenciais.repositories.SequenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class CodigoService {

    private final SequenciaRepository sequenciaRepository;

    public CodigoService(SequenciaRepository sequenciaRepository) {
        this.sequenciaRepository = sequenciaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String gerarCodigoCE() {
        return gerarCodigo(TipoSequencia.CE);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String gerarCodigoCD() {
        return gerarCodigo(TipoSequencia.CD);
    }

    private String gerarCodigo(TipoSequencia tipo) {
        int anoAtual = Year.now().getValue();
        SequenciaContador contador = sequenciaRepository
                .findByTipoAndAnoForUpdate(tipo, anoAtual)
                .orElseGet(() -> sequenciaRepository.save(new SequenciaContador(tipo, anoAtual)));
        contador.setUltimoNumero(contador.getUltimoNumero() + 1);
        sequenciaRepository.save(contador);
        return String.format("%s-%d-%04d", tipo.name(), anoAtual, contador.getUltimoNumero());
    }
}

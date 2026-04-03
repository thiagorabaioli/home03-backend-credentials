package home03.credenciais.services;

import home03.credenciais.entities.AuditLog;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.HistoricoEstado;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.AuditLogRepository;
import home03.credenciais.repositories.HistoricoEstadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final HistoricoEstadoRepository historicoRepository;
    private final AuditLogRepository auditLogRepository;

    public AuditService(HistoricoEstadoRepository historicoRepository,
                        AuditLogRepository auditLogRepository) {
        this.historicoRepository = historicoRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /** Transição de estado: regista em HistoricoEstado (por credencial) E em AuditLog (global, permanente) */
    @Transactional
    public void registarTransicao(Credencial credencial, EstadoCredencial estadoAnterior,
                                  EstadoCredencial estadoNovo, String responsavel, String observacoes) {
        // Histórico por credencial (eliminado em cascade com a credencial)
        HistoricoEstado historico = new HistoricoEstado(credencial, estadoAnterior, estadoNovo, responsavel, observacoes);
        historicoRepository.save(historico);

        // Auditoria global (sobrevive à eliminação da credencial)
        AuditLog log = new AuditLog();
        log.setCodigoCredencial(credencial.getCodigoInterno());
        log.setCodigoColaborador(credencial.getColaborador() != null
                ? credencial.getColaborador().getCodigoInterno() : null);
        log.setAcao("TRANSICAO_ESTADO");
        log.setDetalhe(estadoAnterior + " → " + estadoNovo
                + (observacoes != null && !observacoes.isBlank() ? " | " + observacoes : ""));
        log.setUtilizador(responsavel);
        auditLogRepository.save(log);
    }

    /** Registo de nova credencial */
    @Transactional
    public void registarCriacao(Credencial credencial, String utilizador) {
        AuditLog log = new AuditLog();
        log.setCodigoCredencial(credencial.getCodigoInterno());
        log.setCodigoColaborador(credencial.getColaborador() != null
                ? credencial.getColaborador().getCodigoInterno() : null);
        log.setAcao("REGISTO");
        log.setDetalhe("Credencial criada. Empresa: " + (credencial.getEmpresa() != null
                ? credencial.getEmpresa().getNome() : "?")
                + " | Tipo: " + credencial.getTipoColaborador());
        log.setUtilizador(utilizador);
        auditLogRepository.save(log);
    }

    /** Eliminação de credencial — chamado ANTES de apagar o registo */
    @Transactional
    public void registarEliminacao(String codigoCredencial, String codigoColaborador,
                                   String colaboradorNome, String empresaNome, String utilizador) {
        AuditLog log = new AuditLog();
        log.setCodigoCredencial(codigoCredencial);
        log.setCodigoColaborador(codigoColaborador);
        log.setAcao("ELIMINACAO");
        log.setDetalhe("Credencial eliminada permanentemente."
                + " Colaborador: " + colaboradorNome
                + " | Empresa: " + empresaNome);
        log.setUtilizador(utilizador);
        auditLogRepository.save(log);
    }

    /** Edição de campos da credencial */
    @Transactional
    public void registarEdicao(Credencial credencial, String detalhe, String utilizador) {
        AuditLog log = new AuditLog();
        log.setCodigoCredencial(credencial.getCodigoInterno());
        log.setCodigoColaborador(credencial.getColaborador() != null
                ? credencial.getColaborador().getCodigoInterno() : null);
        log.setAcao("EDICAO");
        log.setDetalhe(detalhe);
        log.setUtilizador(utilizador);
        auditLogRepository.save(log);
    }
}

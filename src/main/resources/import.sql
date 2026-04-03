-- =============================================================
-- import.sql — dados de teste para perfil H2 (test)
-- Executado pelo Hibernate após ddl-auto=create
-- REGRA: sem comentários dentro de blocos VALUES
-- =============================================================

-- SEQUÊNCIAS
INSERT INTO tb_sequencia_contador (id, tipo, ano, ultimo_numero) VALUES ('90000000-0000-0000-0000-000000000001', 'CE', 2026, 5);
INSERT INTO tb_sequencia_contador (id, tipo, ano, ultimo_numero) VALUES ('90000000-0000-0000-0000-000000000002', 'CD', 2026, 7);

-- RESPONSÁVEIS DE MERCADO
INSERT INTO tb_responsavel_mercado (id, nome, email, telefone, ativo) VALUES ('10000000-0000-0000-0000-000000000001', 'António Rodrigues', 'antonio.rodrigues@abc.pt', '912000001', TRUE);
INSERT INTO tb_responsavel_mercado (id, nome, email, telefone, ativo) VALUES ('10000000-0000-0000-0000-000000000002', 'Sofia Martins', 'sofia.martins@xyz.pt', '912000002', TRUE);

-- EMPRESAS
INSERT INTO tb_empresa (id, nome, nif, morada, responsavel_id) VALUES ('20000000-0000-0000-0000-000000000001', 'Empresa ABC Lda', '500000001', 'Rua das Flores 10, Lisboa', '10000000-0000-0000-0000-000000000001');
INSERT INTO tb_empresa (id, nome, nif, morada, responsavel_id) VALUES ('20000000-0000-0000-0000-000000000002', 'Comercial XYZ SA', '500000002', 'Av. da Liberdade 50, Porto', '10000000-0000-0000-0000-000000000002');

-- COLABORADORES EXTERNOS
INSERT INTO tb_colaborador_externo (id, codigo_interno, nome, email, telefone, data_nascimento) VALUES ('30000000-0000-0000-0000-000000000001', 'CE-2026-0001', 'João Silva', 'joao.silva@email.com', '912111001', '1990-05-15');
INSERT INTO tb_colaborador_externo (id, codigo_interno, nome, email, telefone, data_nascimento) VALUES ('30000000-0000-0000-0000-000000000002', 'CE-2026-0002', 'Maria Santos', 'maria.santos@email.com', '912111002', '1985-11-22');
INSERT INTO tb_colaborador_externo (id, codigo_interno, nome, email, telefone, data_nascimento) VALUES ('30000000-0000-0000-0000-000000000003', 'CE-2026-0003', 'Carlos Mendes', 'carlos.mendes@email.com', '912111003', '1978-03-08');
INSERT INTO tb_colaborador_externo (id, codigo_interno, nome, email, telefone, data_nascimento) VALUES ('30000000-0000-0000-0000-000000000004', 'CE-2026-0004', 'Ana Ferreira', 'ana.ferreira@email.com', '912111004', '1993-07-19');
INSERT INTO tb_colaborador_externo (id, codigo_interno, nome, email, telefone, data_nascimento) VALUES ('30000000-0000-0000-0000-000000000005', 'CE-2026-0005', 'Pedro Costa', 'pedro.costa@email.com', '912111005', '1982-12-30');

-- CREDENCIAIS
-- CD-2026-0001  João Silva / ABC / REPOSICAO   → ENTRADA_AUTORIZADA
-- CD-2026-0002  João Silva / ABC / MANUTENCAO  → AGUARDA_VALIDACAO_ES (mesmo CE, tipo diferente)
-- CD-2026-0003  Maria Santos / XYZ / VISITA    → AGUARDA_APROVACAO_RM (tem token activo)
-- CD-2026-0004  Carlos Mendes / ABC / LIMPEZA  → APROVADA
-- CD-2026-0005  Ana Ferreira / XYZ / PROMOCAO  → AGUARDA_VALIDACAO_ES (seguro a expirar em 15 dias)
-- CD-2026-0006  Pedro Costa / ABC / REPOSICAO  → REJEITADA_ES
-- CD-2026-0007  Pedro Costa / XYZ / REPOSICAO  → EXPIRADA (data passada)
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000001', 'CD-2026-0001', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'REPOSICAO', 'ENTRADA_AUTORIZADA', '2026-01-10', '2026-12-31', '2026-01-10 09:00:00');
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000002', 'CD-2026-0002', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'MANUTENCAO', 'AGUARDA_VALIDACAO_ES', '2026-03-01', '2026-12-31', '2026-03-01 10:15:00');
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000003', 'CD-2026-0003', '30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'VISITA', 'AGUARDA_APROVACAO_RM', '2026-04-01', '2026-09-30', '2026-04-01 11:00:00');
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000004', 'CD-2026-0004', '30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001', 'LIMPEZA', 'APROVADA', '2026-02-15', '2026-12-31', '2026-02-15 14:30:00');
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000005', 'CD-2026-0005', '30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000002', 'PROMOCAO', 'AGUARDA_VALIDACAO_ES', '2026-04-02', '2026-12-31', '2026-04-02 08:45:00');
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000006', 'CD-2026-0006', '30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000001', 'REPOSICAO', 'REJEITADA_ES', '2026-02-01', '2026-12-31', '2026-02-01 09:00:00');
INSERT INTO tb_credencial (id, codigo_interno, colaborador_id, empresa_id, tipo_colaborador, estado, data_inicio, data_fim, data_registo) VALUES ('40000000-0000-0000-0000-000000000007', 'CD-2026-0007', '30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000002', 'REPOSICAO', 'EXPIRADA', '2025-01-01', '2025-12-31', '2025-01-01 09:00:00');

-- SEGUROS (documento_id NULL — sem ficheiro real em teste)
-- CD-2026-0005: seguro expira em 15 dias para testar alerta ExpiracaoScheduler
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'APL-2026-001', 'Fidelidade', '2026-01-01', '2026-12-31', NULL);
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', 'APL-2026-002', 'Tranquilidade', '2026-01-01', '2026-12-31', NULL);
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', 'APL-2026-003', 'Allianz', '2026-01-01', '2026-12-31', NULL);
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000004', 'APL-2026-004', 'Fidelidade', '2026-01-01', '2026-12-31', NULL);
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000005', 'APL-2026-005', 'Generali', '2026-01-01', '2026-04-18', NULL);
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000006', 'APL-2026-006', 'Zurich', '2026-01-01', '2026-12-31', NULL);
INSERT INTO tb_seguro (id, credencial_id, apolice, seguradora, data_inicio, data_fim, documento_id) VALUES ('50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000007', 'APL-2025-007', 'Fidelidade', '2025-01-01', '2025-12-31', NULL);

-- FICHAS DE APTIDÃO MÉDICA
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', '2026-01-05', '2026-12-31', 'APTO', NULL);
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', '2026-02-20', '2026-12-31', 'APTO', NULL);
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', '2026-03-10', '2026-09-30', 'APTO', NULL);
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000004', '2026-02-01', '2026-12-31', 'APTO', NULL);
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000005', '2026-03-30', '2026-12-31', 'APTO', NULL);
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000006', '2026-01-15', '2026-12-31', 'APTO', NULL);
INSERT INTO tb_ficha_aptidao_medica (id, credencial_id, data_emissao, data_validade, resultado, documento_id) VALUES ('60000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000007', '2025-01-05', '2025-12-31', 'NAO_APTO', NULL);

-- HORÁRIOS DE TRABALHO
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', '07:00:00', '15:00:00');
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', '08:00:00', '17:00:00');
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', '09:00:00', '18:00:00');
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000004', '06:00:00', '14:00:00');
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000005', '10:00:00', '19:00:00');
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000006', '08:00:00', '17:00:00');
INSERT INTO tb_horario_trabalho (id, credencial_id, hora_entrada, hora_saida) VALUES ('70000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000007', '07:00:00', '16:00:00');

-- DIAS DA SEMANA
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000001', 'SEGUNDA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000001', 'TERCA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000001', 'QUARTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000001', 'QUINTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000001', 'SEXTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000002', 'SEGUNDA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000002', 'QUARTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000002', 'SEXTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000003', 'TERCA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000003', 'QUINTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000004', 'SEGUNDA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000004', 'TERCA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000004', 'QUARTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000004', 'QUINTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000004', 'SEXTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000005', 'SEGUNDA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000005', 'QUARTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000006', 'SEGUNDA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000006', 'TERCA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000006', 'QUARTA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000007', 'SEGUNDA');
INSERT INTO tb_horario_dias (horario_id, dia) VALUES ('70000000-0000-0000-0000-000000000007', 'SEXTA');

-- TOKEN DE APROVAÇÃO — activo para CD-2026-0003 (AGUARDA_APROVACAO_RM), expira 2026-04-05
INSERT INTO tb_token_aprovacao (id, credencial_id, tipo, data_expiracao, utilizado) VALUES ('a0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000003', 'APROVACAO_RM', '2026-04-05 11:00:00', FALSE);

-- HISTÓRICO DE ESTADOS — um INSERT por linha para compatibilidade com Hibernate import.sql
-- CD-2026-0001: caminho completo até ENTRADA_AUTORIZADA
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2026-01-10 09:01:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000001', 'AGUARDA_VALIDACAO_ES', 'AGUARDA_APROVACAO_RM', 'es.operador@abc.pt', 'Documentos validados', '2026-01-11 10:00:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000001', 'AGUARDA_APROVACAO_RM', 'APROVADA', 'antonio.rodrigues@abc.pt', 'Colaborador aprovado pelo RM', '2026-01-12 14:00:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000001', 'APROVADA', 'ENTRADA_AUTORIZADA', 'es.operador@abc.pt', 'Entrada autorizada manualmente', '2026-01-13 08:30:00');
-- CD-2026-0002
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000002', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2026-03-01 10:16:00');
-- CD-2026-0003
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000003', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2026-04-01 11:01:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000003', 'AGUARDA_VALIDACAO_ES', 'AGUARDA_APROVACAO_RM', 'es.operador@abc.pt', 'Documentos validados, link enviado ao RM', '2026-04-02 09:00:00');
-- CD-2026-0004
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000008', '40000000-0000-0000-0000-000000000004', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2026-02-15 14:31:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000004', 'AGUARDA_VALIDACAO_ES', 'AGUARDA_APROVACAO_RM', 'es.operador@abc.pt', 'Documentos ok', '2026-02-16 10:00:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000004', 'AGUARDA_APROVACAO_RM', 'APROVADA', 'antonio.rodrigues@abc.pt', 'RM aprovou', '2026-02-17 15:00:00');
-- CD-2026-0005
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000011', '40000000-0000-0000-0000-000000000005', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2026-04-02 08:46:00');
-- CD-2026-0006
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000012', '40000000-0000-0000-0000-000000000006', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2026-02-01 09:01:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000013', '40000000-0000-0000-0000-000000000006', 'AGUARDA_VALIDACAO_ES', 'REJEITADA_ES', 'es.operador@abc.pt', 'Apolice de seguro invalida', '2026-02-02 11:00:00');
-- CD-2026-0007: ciclo completo até EXPIRADA
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000014', '40000000-0000-0000-0000-000000000007', 'PENDENTE', 'AGUARDA_VALIDACAO_ES', 'SISTEMA', 'Registo publico submetido', '2025-01-01 09:01:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000015', '40000000-0000-0000-0000-000000000007', 'AGUARDA_VALIDACAO_ES', 'AGUARDA_APROVACAO_RM', 'es.operador@abc.pt', 'Documentos validados', '2025-01-02 10:00:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000016', '40000000-0000-0000-0000-000000000007', 'AGUARDA_APROVACAO_RM', 'APROVADA', 'sofia.martins@xyz.pt', 'RM aprovou', '2025-01-03 14:00:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000017', '40000000-0000-0000-0000-000000000007', 'APROVADA', 'ENTRADA_AUTORIZADA', 'es.operador@abc.pt', 'Entrada autorizada', '2025-01-04 08:00:00');
INSERT INTO tb_historico_estado (id, credencial_id, estado_anterior, estado_novo, responsavel, observacoes, data_transicao) VALUES ('80000000-0000-0000-0000-000000000018', '40000000-0000-0000-0000-000000000007', 'ENTRADA_AUTORIZADA', 'EXPIRADA', 'SISTEMA', 'Credencial expirada automaticamente', '2026-01-01 04:00:00');

-- Dados de teste: credenciais de exemplo
INSERT INTO tb_credencial (id, nome, email, data_nascimento, empresa, tipo, email_responsavel, data_validade_credencial, data_validade_ficha_aptidao, num_apolice, data_validade_seguro, estado, token_validacao, data_criacao)
VALUES ('a1b2c3d4-0001-0001-0001-000000000001', 'João Silva', 'joao.silva@empresa.com', '1990-05-15', 'Empresa ABC Lda', 'REPOSICAO', 'responsavel@empresa.com', '2026-12-31', '2026-06-30', 'APL-2024-001', '2026-12-31', 'APROVADA', 'token-teste-aprovada-001', NOW());

INSERT INTO tb_credencial (id, nome, email, data_nascimento, empresa, tipo, email_responsavel, data_validade_credencial, data_validade_ficha_aptidao, num_apolice, data_validade_seguro, estado, token_validacao, data_criacao)
VALUES ('a1b2c3d4-0002-0002-0002-000000000002', 'Maria Santos', 'maria.santos@comercial.pt', '1985-11-22', 'Comercial XYZ', 'VISITA', 'responsavel@comercial.pt', '2026-03-31', '2025-12-31', 'APL-2024-002', '2026-03-31', 'AGUARDA_VALIDACAO', 'token-teste-aguarda-002', NOW());

INSERT INTO tb_credencial (id, nome, email, data_nascimento, empresa, tipo, email_responsavel, data_validade_credencial, data_validade_ficha_aptidao, num_apolice, data_validade_seguro, estado, token_validacao, data_criacao)
VALUES ('a1b2c3d4-0003-0003-0003-000000000003', 'Carlos Mendes', 'carlos.mendes@manutencao.pt', '1978-03-08', 'TecnoServ Manutenção', 'MANUTENCAO', 'chefe@tecnoserv.pt', '2025-06-30', '2025-09-30', 'APL-2024-003', '2025-06-30', 'ENTRADA_AUTORIZADA', 'token-teste-entrada-003', NOW());

INSERT INTO tb_credencial (id, nome, email, data_nascimento, empresa, tipo, email_responsavel, data_validade_credencial, data_validade_ficha_aptidao, num_apolice, data_validade_seguro, estado, token_validacao, data_criacao)
VALUES ('a1b2c3d4-0004-0004-0004-000000000004', 'Ana Ferreira', 'ana.ferreira@limpeza.pt', '1993-07-19', 'CleanPro Serviços', 'LIMPEZA', 'supervisor@cleanpro.pt', '2026-01-31', '2026-01-31', 'APL-2024-004', '2026-01-31', 'PENDENTE', 'token-teste-pendente-004', NOW());

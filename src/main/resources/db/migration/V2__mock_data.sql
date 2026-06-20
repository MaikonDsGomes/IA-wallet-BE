-- V2__mock_data.sql
-- =============================================================
-- V2 - Dados de mock para testes
-- 3 usuários com perfis diferentes: Conservador, Moderado, Agressivo
-- =============================================================

-- =============================================================
-- USUÁRIOS (senha: Test@1234 em bcrypt)
-- =============================================================
INSERT INTO usuario (nome, email, senha, status, role) VALUES
('Ana Conservadora', 'ana@investwallet.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', b'1', 'USER'),
('Bruno Moderado',   'bruno@investwallet.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', b'1', 'USER'),
('Carlos Agressivo', 'carlos@investwallet.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', b'1', 'USER');

-- =============================================================
-- CONTAS
-- =============================================================
INSERT INTO conta (usuario_id, saldo, margem_disponivel) VALUES
(1, 5000.00,  10),  -- Ana
(2, 12000.00, 25),  -- Bruno
(3, 3000.00,  50);  -- Carlos

-- =============================================================
-- CARTEIRAS
-- =============================================================
INSERT INTO carteira (conta_id, valor_total, rentabilidade_total, rentabilidade_mes, data_ultimo_snapshot) VALUES
(1, 45000.00,  8.5000,  0.9000, NOW()),  -- Ana
(2, 87000.00, 14.2000,  1.8000, NOW()),  -- Bruno
(3, 32000.00, 22.7000,  3.1000, NOW());  -- Carlos

-- =============================================================
-- ATIVOS
-- =============================================================
INSERT INTO ativo (nome, tipo, volatilidade, ativo) VALUES
('Tesouro IPCA+ 2029',  'RENDA_FIXA', 1, b'1'),  -- 1
('XPML11',              'FII',        3, b'1'),  -- 2
('PETR4',               'ACAO',       4, b'1'),  -- 3
('VALE3',               'ACAO',       4, b'1'),  -- 4
('Bitcoin',             'CRIPTO',     9, b'1'),  -- 5
('BOVA11',              'ETF',        3, b'1'),  -- 6
('LCI Bradesco 2025',   'RENDA_FIXA', 1, b'1'),  -- 7
('MXRF11',              'FII',        2, b'1'),  -- 8
('Ethereum',            'CRIPTO',     8, b'1'),  -- 9
('IVVB11',              'ETF',        3, b'1'); -- 10

-- =============================================================
-- POSIÇÕES
-- Ana: perfil conservador → renda fixa + FIIs
-- Bruno: perfil moderado → mix equilibrado
-- Carlos: perfil agressivo → ações + cripto
-- =============================================================

-- Ana (carteira_id = 1)
INSERT INTO posicao (carteira_id, ativo_id, quantidade, preco_medio) VALUES
(1, 1, 20,  52.30),   -- Tesouro IPCA+
(1, 7, 10, 100.00),   -- LCI Bradesco
(1, 2, 50,  98.50),   -- XPML11
(1, 8, 80,  10.20);   -- MXRF11

-- Bruno (carteira_id = 2)
INSERT INTO posicao (carteira_id, ativo_id, quantidade, preco_medio) VALUES
(2, 3, 100,  32.40),  -- PETR4
(2, 4,  80,  68.90),  -- VALE3
(2, 6,  60, 102.30),  -- BOVA11
(2, 2,  40,  98.50),  -- XPML11
(2, 1,  10,  52.30);  -- Tesouro IPCA+

-- Carlos (carteira_id = 3)
INSERT INTO posicao (carteira_id, ativo_id, quantidade, preco_medio) VALUES
(3, 3, 200,  32.40),  -- PETR4
(3, 4, 150,  68.90),  -- VALE3
(3, 5,   2, 280000.00), -- Bitcoin
(3, 9,  10,  18000.00); -- Ethereum

-- =============================================================
-- PERFIL DE RISCO
-- =============================================================
INSERT INTO perfil_risco (usuario_id, score, nivel, margem_minima) VALUES
(1, 25,  'CONSERVADOR', 5),
(2, 55,  'MODERADO',    20),
(3, 85,  'AGRESSIVO',   40);

-- =============================================================
-- SCORE CARTEIRA
-- =============================================================
INSERT INTO score_carteira (
    carteira_id,
    score_total, score_diversificacao, score_risco, score_concentracao, score_performance,
    num_ativos, num_classes, concentracao_maxima, top3_concentracao, indice_herfindahl,
    num_setores, volatilidade, beta_carteira, var_95, vs_cdi, vs_ibov, sharpe_ratio
) VALUES
-- Ana: conservadora, bem diversificada em renda fixa
(1, 82, 88, 90, 78, 72,
 4, 2, 28.50, 62.00, 0.1820,
 2, 'Baixa', 0.3200, -0.8000, 1.2000, -3.5000, 1.10),

-- Bruno: moderado, mix equilibrado
(2, 76, 85, 72, 68, 78,
 5, 3, 35.00, 65.00, 0.1950,
 4, 'Media', 0.8500, -2.3000, 2.4000, -0.8000, 1.45),

-- Carlos: agressivo, concentrado em cripto e ações
(3, 54, 60, 45, 48, 72,
 4, 2, 52.00, 88.00, 0.3200,
 2, 'Alta', 1.6500, -8.5000, 5.8000, 3.2000, 0.95);

-- =============================================================
-- ORDENS (histórico de compras)
-- =============================================================
INSERT INTO ordem (conta_id, ativo_id, tipo, quantidade, preco_execucao, status) VALUES
(1, 1, 'COMPRA', 20,  52.30, 'EXECUTADA'),
(1, 2, 'COMPRA', 50,  98.50, 'EXECUTADA'),
(2, 3, 'COMPRA', 100, 32.40, 'EXECUTADA'),
(2, 4, 'COMPRA', 80,  68.90, 'EXECUTADA'),
(3, 5, 'COMPRA', 2,   280000.00, 'EXECUTADA'),
(3, 9, 'COMPRA', 10,  18000.00, 'EXECUTADA');

-- =============================================================
-- MOVIMENTAÇÕES
-- =============================================================
INSERT INTO movimentacao (conta_id, tipo, valor) VALUES
(1, 'DEPOSITO', 50000.00),
(2, 'DEPOSITO', 100000.00),
(3, 'DEPOSITO', 35000.00),
(2, 'SAQUE',    13000.00);

-- =============================================================
-- ALERTAS DE RISCO
-- =============================================================
INSERT INTO risco_alerta (conta_id, nivel, mensagem) VALUES
(3, 8, 'Concentração em cripto acima de 50% do patrimônio'),
(3, 6, 'Alta volatilidade detectada na carteira'),
(2, 3, 'Concentração em PETR4 acima do recomendado');
-- V3 - Adiciona coluna codigo (ticker) na tabela ativo
ALTER TABLE ativo
    ADD COLUMN codigo VARCHAR(20) NOT NULL DEFAULT '' AFTER nome;

-- Atualiza os ativos existentes com seus códigos
UPDATE ativo SET codigo = 'IPCA2029'  WHERE id = 1;  -- Tesouro IPCA+ 2029
UPDATE ativo SET codigo = 'XPML11'    WHERE id = 2;
UPDATE ativo SET codigo = 'PETR4'     WHERE id = 3;
UPDATE ativo SET codigo = 'VALE3'     WHERE id = 4;
UPDATE ativo SET codigo = 'BTC'       WHERE id = 5;
UPDATE ativo SET codigo = 'BOVA11'    WHERE id = 6;
UPDATE ativo SET codigo = 'LCIBRAD25' WHERE id = 7;  -- LCI Bradesco 2025
UPDATE ativo SET codigo = 'MXRF11'    WHERE id = 8;
UPDATE ativo SET codigo = 'ETH'       WHERE id = 9;
UPDATE ativo SET codigo = 'IVVB11'    WHERE id = 10;
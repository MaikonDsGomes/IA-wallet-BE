ALTER TABLE score_carteira
    MODIFY score_total INT NULL,
    MODIFY score_diversificacao INT NULL,
    MODIFY score_risco INT NULL,
    MODIFY score_concentracao INT NULL,
    MODIFY score_performance INT NULL,
    MODIFY concentracao_maxima DECIMAL(5,2) NULL,
    MODIFY top3_concentracao DECIMAL(5,2) NULL,
    MODIFY indice_herfindahl DECIMAL(6,4) NULL,
    MODIFY num_setores INT NULL,
    MODIFY volatilidade ENUM('Baixa','Media','Alta') NULL,
    MODIFY beta_carteira DECIMAL(6,4) NULL,
    MODIFY var_95 DECIMAL(8,4) NULL,
    MODIFY vs_cdi DECIMAL(8,4) NULL,
    MODIFY vs_ibov DECIMAL(8,4) NULL,
    MODIFY sharpe_ratio DECIMAL(6,4) NULL;

ALTER TABLE score_carteira
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PARCIAL',
    ADD COLUMN versao_metodologia VARCHAR(30) NOT NULL DEFAULT 'v1',
    ADD COLUMN perfil_risco VARCHAR(20) NULL;

CREATE TABLE recalculo_score (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    carteira_id       BIGINT       NOT NULL,
    agendado_para     DATETIME     NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
    tentativas        INT          NOT NULL DEFAULT 0,
    versao_alteracao  INT          NOT NULL DEFAULT 1,
    ultimo_erro       VARCHAR(1000),
    processado_em     DATETIME,
    criado_em         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version           BIGINT,
    CONSTRAINT uk_recalculo_score_carteira UNIQUE (carteira_id),
    CONSTRAINT fk_recalculo_score_carteira FOREIGN KEY (carteira_id)
        REFERENCES carteira(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_recalculo_score_vencido
    ON recalculo_score(status, agendado_para, atualizado_em);

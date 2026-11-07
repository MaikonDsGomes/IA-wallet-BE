CREATE TABLE market_series (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    instrumento     VARCHAR(50) NOT NULL,
    tipo            VARCHAR(20) NOT NULL,
    data_referencia DATE        NOT NULL,
    valor           DECIMAL(18,8),
    valor_ajustado  DECIMAL(18,8),
    fonte           VARCHAR(40) NOT NULL,
    coletado_em     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_market_series_point UNIQUE (instrumento, tipo, data_referencia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_market_series_lookup
    ON market_series (instrumento, tipo, data_referencia);

CREATE TABLE ativo_setor (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    ativo_id     BIGINT       NOT NULL,
    setor        VARCHAR(150) NOT NULL,
    fonte        VARCHAR(40)  NOT NULL,
    atualizado_em DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ativo_setor_ativo UNIQUE (ativo_id),
    CONSTRAINT fk_ativo_setor_ativo FOREIGN KEY (ativo_id)
        REFERENCES ativo(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ativo_setor_setor ON ativo_setor (setor);

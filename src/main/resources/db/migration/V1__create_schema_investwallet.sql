-- =============================================================
-- V1 - Schema completo do InvestWallet
-- Tabelas: usuario, conta, carteira, ativo, posicao,
--          ordem, movimentacao, risco_alerta, perfil_risco,
--          cotacao, score_carteira, recomendacao,
--          noticia, noticia_ativo, noticia_cache_usuario
-- =============================================================

CREATE TABLE usuario (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome   VARCHAR(200)  NOT NULL,
    email  VARCHAR(255)  NOT NULL,
    senha  VARCHAR(1000) NOT NULL,
    status BIT(1)        NOT NULL DEFAULT b'1',
    role   VARCHAR(45)   NOT NULL DEFAULT 'USER',
    criado DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_usuario_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_usuario_email ON usuario(email);

-- ----------------------------------------------------------

CREATE TABLE conta (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id        BIGINT         NOT NULL,
    saldo             DECIMAL(15,2)  NOT NULL DEFAULT 0.00,
    margem_disponivel INT            NOT NULL DEFAULT 0,
    CONSTRAINT fk_conta_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_conta_usuario ON conta(usuario_id);

-- ----------------------------------------------------------

CREATE TABLE carteira (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    conta_id             BIGINT        NOT NULL,
    valor_total          DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    rentabilidade_total  DECIMAL(8,4)  NOT NULL DEFAULT 0.0000,
    rentabilidade_mes    DECIMAL(8,4)  NOT NULL DEFAULT 0.0000,
    data_ultimo_snapshot DATETIME,
    CONSTRAINT fk_carteira_conta FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_carteira_conta ON carteira(conta_id);

-- ----------------------------------------------------------

CREATE TABLE ativo (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    tipo         ENUM('ACAO','FII','CRIPTO','RENDA_FIXA','ETF') NOT NULL,
    volatilidade INT          NOT NULL DEFAULT 0,
    ativo        BIT(1)       NOT NULL DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------

CREATE TABLE posicao (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    carteira_id BIGINT        NOT NULL,
    ativo_id    BIGINT        NOT NULL,
    quantidade  INT           NOT NULL DEFAULT 0,
    preco_medio DECIMAL(15,4) NOT NULL DEFAULT 0.0000,
    CONSTRAINT fk_posicao_carteira FOREIGN KEY (carteira_id) REFERENCES carteira(id) ON DELETE CASCADE,
    CONSTRAINT fk_posicao_ativo    FOREIGN KEY (ativo_id)    REFERENCES ativo(id)    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_posicao_carteira ON posicao(carteira_id);
CREATE INDEX idx_posicao_ativo    ON posicao(ativo_id);

-- ----------------------------------------------------------

CREATE TABLE ordem (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    conta_id       BIGINT        NOT NULL,
    ativo_id       BIGINT        NOT NULL,
    tipo           ENUM('COMPRA','VENDA') NOT NULL,
    quantidade     INT           NOT NULL,
    preco_execucao DECIMAL(15,4) NOT NULL DEFAULT 0.0000,
    status         ENUM('PENDENTE','EXECUTADA','CANCELADA') NOT NULL DEFAULT 'PENDENTE',
    criada_em      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ordem_conta FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE,
    CONSTRAINT fk_ordem_ativo FOREIGN KEY (ativo_id) REFERENCES ativo(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ordem_conta  ON ordem(conta_id);
CREATE INDEX idx_ordem_status ON ordem(status);

-- ----------------------------------------------------------

CREATE TABLE movimentacao (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    conta_id         BIGINT        NOT NULL,
    conta_destino_id BIGINT,
    tipo             ENUM('DEPOSITO','SAQUE','TRANSFERENCIA') NOT NULL,
    valor            DECIMAL(15,2) NOT NULL,
    data             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimentacao_conta         FOREIGN KEY (conta_id)         REFERENCES conta(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimentacao_conta_destino FOREIGN KEY (conta_destino_id) REFERENCES conta(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_movimentacao_conta ON movimentacao(conta_id);
CREATE INDEX idx_movimentacao_tipo  ON movimentacao(tipo);

-- ----------------------------------------------------------

CREATE TABLE risco_alerta (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    conta_id  BIGINT       NOT NULL,
    nivel     INT          NOT NULL,
    mensagem  VARCHAR(100),
    criado_em DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risco_alerta_conta FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_risco_alerta_conta ON risco_alerta(conta_id);

-- ----------------------------------------------------------

CREATE TABLE perfil_risco (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id    BIGINT   NOT NULL,
    score         INT      NOT NULL DEFAULT 0,
    nivel         ENUM('CONSERVADOR','MODERADO','AGRESSIVO') NOT NULL DEFAULT 'MODERADO',
    margem_minima INT      NOT NULL DEFAULT 0,
    calculado_em  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_perfil_risco_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_perfil_risco_usuario ON perfil_risco(usuario_id);

-- ----------------------------------------------------------

CREATE TABLE cotacao (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    ativo_id    BIGINT        NOT NULL,
    preco       DECIMAL(15,4) NOT NULL,
    variacao    DECIMAL(8,4)  NOT NULL DEFAULT 0.0000,
    coletado_em DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cotacao_ativo FOREIGN KEY (ativo_id) REFERENCES ativo(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_cotacao_ativo_data ON cotacao(ativo_id, coletado_em DESC);

CREATE OR REPLACE VIEW vw_cotacao_atual AS
SELECT c.*
FROM cotacao c
INNER JOIN (
    SELECT ativo_id, MAX(coletado_em) AS ultima
    FROM cotacao
    GROUP BY ativo_id
) recente ON recente.ativo_id = c.ativo_id
         AND recente.ultima   = c.coletado_em;

-- ----------------------------------------------------------

CREATE TABLE score_carteira (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    carteira_id          BIGINT       NOT NULL,
    score_total          INT          NOT NULL DEFAULT 0,
    score_diversificacao INT          NOT NULL DEFAULT 0,
    score_risco          INT          NOT NULL DEFAULT 0,
    score_concentracao   INT          NOT NULL DEFAULT 0,
    score_performance    INT          NOT NULL DEFAULT 0,
    num_ativos           INT          NOT NULL DEFAULT 0,
    num_classes          INT          NOT NULL DEFAULT 0,
    concentracao_maxima  DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    top3_concentracao    DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    indice_herfindahl    DECIMAL(6,4) NOT NULL DEFAULT 0.0000,
    num_setores          INT          NOT NULL DEFAULT 0,
    volatilidade         ENUM('Baixa','Media','Alta') NOT NULL DEFAULT 'Media',
    beta_carteira        DECIMAL(6,4) NOT NULL DEFAULT 0.0000,
    var_95               DECIMAL(8,4) NOT NULL DEFAULT 0.0000,
    vs_cdi               DECIMAL(8,4) NOT NULL DEFAULT 0.0000,
    vs_ibov              DECIMAL(8,4) NOT NULL DEFAULT 0.0000,
    sharpe_ratio         DECIMAL(6,4) NOT NULL DEFAULT 0.0000,
    calculado_em         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_score_carteira FOREIGN KEY (carteira_id) REFERENCES carteira(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_score_carteira  ON score_carteira(carteira_id);
CREATE INDEX idx_score_calculado ON score_carteira(carteira_id, calculado_em DESC);

-- ----------------------------------------------------------

CREATE TABLE recomendacao (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    carteira_id BIGINT       NOT NULL,
    ativo_id    BIGINT,
    titulo      VARCHAR(150) NOT NULL,
    descricao   VARCHAR(500) NOT NULL,
    categoria   ENUM('RISCO','OPORTUNIDADE','POSITIVO')  NOT NULL,
    prioridade  ENUM('ALTA','MEDIA','BAIXA')             NOT NULL DEFAULT 'MEDIA',
    impacto_pts INT          NOT NULL DEFAULT 0,
    ativa       BIT(1)       NOT NULL DEFAULT b'1',
    gerada_em   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recomendacao_carteira FOREIGN KEY (carteira_id) REFERENCES carteira(id) ON DELETE CASCADE,
    CONSTRAINT fk_recomendacao_ativo    FOREIGN KEY (ativo_id)    REFERENCES ativo(id)    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_recomendacao_carteira  ON recomendacao(carteira_id, ativa, prioridade);
CREATE INDEX idx_recomendacao_categoria ON recomendacao(carteira_id, categoria);

-- ----------------------------------------------------------

CREATE TABLE noticia (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo       VARCHAR(300)  NOT NULL,
    descricao    VARCHAR(1000),
    fonte        VARCHAR(100)  NOT NULL,
    url          VARCHAR(2000) NOT NULL,
    url_hash     CHAR(64)      AS (SHA2(url, 256)) STORED NOT NULL,
    sentimento   ENUM('POSITIVO','NEGATIVO','NEUTRO') NOT NULL DEFAULT 'NEUTRO',
    publicada_em DATETIME      NOT NULL,
    criada_em    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_noticia_url_hash UNIQUE (url_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_noticia_publicada  ON noticia(publicada_em DESC);
CREATE INDEX idx_noticia_sentimento ON noticia(sentimento);

-- ----------------------------------------------------------

CREATE TABLE noticia_ativo (
    noticia_id BIGINT NOT NULL,
    ativo_id   BIGINT NOT NULL,
    PRIMARY KEY (noticia_id, ativo_id),
    CONSTRAINT fk_noticia_ativo_noticia FOREIGN KEY (noticia_id) REFERENCES noticia(id) ON DELETE CASCADE,
    CONSTRAINT fk_noticia_ativo_ativo   FOREIGN KEY (ativo_id)   REFERENCES ativo(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_noticia_ativo_ativo ON noticia_ativo(ativo_id);

-- ----------------------------------------------------------

CREATE TABLE noticia_cache_usuario (
    id         BIGINT   AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT   NOT NULL,
    noticia_id BIGINT   NOT NULL,
    lida       BIT(1)   NOT NULL DEFAULT b'0',
    exibida_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cache_usuario_noticia UNIQUE (usuario_id, noticia_id),
    CONSTRAINT fk_cache_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_cache_noticia FOREIGN KEY (noticia_id) REFERENCES noticia(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_cache_usuario      ON noticia_cache_usuario(usuario_id);
CREATE INDEX idx_cache_usuario_lida ON noticia_cache_usuario(usuario_id, lida);
CREATE INDEX idx_cache_exibida      ON noticia_cache_usuario(exibida_em DESC);
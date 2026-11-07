CREATE TABLE score_email_notification (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    recalculo_id  BIGINT       NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PROCESSANDO',
    tentativas    INT          NOT NULL DEFAULT 0,
    ultimo_erro   VARCHAR(1000),
    enviado_em    DATETIME,
    criado_em     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_score_email_notification_recalculo UNIQUE (recalculo_id),
    CONSTRAINT fk_score_email_notification_recalculo FOREIGN KEY (recalculo_id)
        REFERENCES recalculo_score(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

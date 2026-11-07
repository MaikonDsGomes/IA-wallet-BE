package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.StatusScoreEmailNotification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_email_notification",
        uniqueConstraints = @UniqueConstraint(name = "uk_score_email_notification_recalculo",
                columnNames = "recalculo_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreEmailNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recalculo_id", nullable = false)
    private Long recalculoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusScoreEmailNotification status;

    @Column(nullable = false)
    private Integer tentativas;

    @Column(name = "ultimo_erro", length = 1000)
    private String ultimoErro;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        if (status == null) status = StatusScoreEmailNotification.PROCESSANDO;
        if (tentativas == null) tentativas = 0;
        if (criadoEm == null) criadoEm = now;
        if (atualizadoEm == null) atualizadoEm = now;
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}

package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.StatusRecalculoScore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recalculo_score", uniqueConstraints = @UniqueConstraint(name = "uk_recalculo_score_carteira", columnNames = "carteira_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculoScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private CarteiraEntity carteira;

    @Column(name = "agendado_para", nullable = false)
    private LocalDateTime agendadoPara;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusRecalculoScore status;

    @Column(nullable = false)
    private Integer tentativas;

    @Column(name = "versao_alteracao", nullable = false)
    private Integer versaoAlteracao;

    @Column(name = "ultimo_erro", length = 1000)
    private String ultimoErro;

    @Column(name = "processado_em")
    private LocalDateTime processadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        if (status == null) status = StatusRecalculoScore.PENDENTE;
        if (tentativas == null) tentativas = 0;
        if (versaoAlteracao == null) versaoAlteracao = 1;
        if (criadoEm == null) criadoEm = now;
        if (atualizadoEm == null) atualizadoEm = now;
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}

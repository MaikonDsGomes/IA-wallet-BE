package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoOrdem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaEntity conta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id", nullable = false)
    private AtivoEntity ativo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOrdem tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_execucao", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoExecucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrdem status;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    protected void onCreate() {
        if (criadaEm == null) criadaEm = LocalDateTime.now();
        if (status == null) status = StatusOrdem.PENDENTE;
        if (precoExecucao == null) precoExecucao = BigDecimal.ZERO;
    }
}

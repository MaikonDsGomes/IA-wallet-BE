package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "posicao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosicaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private CarteiraEntity carteira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id", nullable = false)
    private AtivoEntity ativo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_medio", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoMedio;

    @PrePersist
    protected void onCreate() {
        if (quantidade == null) quantidade = 0;
        if (precoMedio == null) precoMedio = BigDecimal.ZERO;
    }
}

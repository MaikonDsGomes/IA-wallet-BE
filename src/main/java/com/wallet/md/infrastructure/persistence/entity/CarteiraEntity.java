package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carteira")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarteiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaEntity conta;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @PrePersist
    protected void onCreate() {
        if (valorTotal == null) valorTotal = BigDecimal.ZERO;

    }
}

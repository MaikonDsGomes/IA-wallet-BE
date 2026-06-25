package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotacao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id", nullable = false)
    private AtivoEntity ativo;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal preco;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal variacao;

    @Column(name = "coletado_em", nullable = false)
    private LocalDateTime coletadoEm;
}
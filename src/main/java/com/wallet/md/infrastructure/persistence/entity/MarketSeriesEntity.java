package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_series",
        uniqueConstraints = @UniqueConstraint(name = "uk_market_series_point",
                columnNames = {"instrumento", "tipo", "data_referencia"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSeriesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String instrumento;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Column(precision = 18, scale = 8)
    private BigDecimal valor;

    @Column(name = "valor_ajustado", precision = 18, scale = 8)
    private BigDecimal valorAjustado;

    @Column(nullable = false, length = 40)
    private String fonte;

    @Column(name = "coletado_em", nullable = false)
    private LocalDateTime coletadoEm;
}

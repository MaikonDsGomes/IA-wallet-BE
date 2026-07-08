package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_carteira")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCarteiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private CarteiraEntity carteira;

    @Column(name = "score_total", nullable = false)
    private Integer scoreTotal;

    @Column(name = "score_diversificacao", nullable = false)
    private Integer scoreDiversificacao;

    @Column(name = "score_risco", nullable = false)
    private Integer scoreRisco;

    @Column(name = "score_concentracao", nullable = false)
    private Integer scoreConcentracao;

    @Column(name = "score_performance", nullable = false)
    private Integer scorePerformance;

    @Column(name = "num_ativos", nullable = false)
    private Integer numAtivos;

    @Column(name = "num_classes", nullable = false)
    private Integer numClasses;

    @Column(name = "concentracao_maxima", nullable = false, precision = 5, scale = 2)
    private BigDecimal concentracaoMaxima;

    @Column(name = "top3_concentracao", nullable = false, precision = 5, scale = 2)
    private BigDecimal top3Concentracao;

    @Column(name = "indice_herfindahl", nullable = false, precision = 6, scale = 4)
    private BigDecimal indiceHerfindahl;

    @Column(name = "num_setores", nullable = false)
    private Integer numSetores;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Volatilidade volatilidade;

    @Column(name = "beta_carteira", nullable = false, precision = 6, scale = 4)
    private BigDecimal betaCarteira;

    @Column(name = "var_95", nullable = false, precision = 8, scale = 4)
    private BigDecimal var95;

    @Column(name = "vs_cdi", nullable = false, precision = 8, scale = 4)
    private BigDecimal vsCdi;

    @Column(name = "vs_ibov", nullable = false, precision = 8, scale = 4)
    private BigDecimal vsIbov;

    @Column(name = "sharpe_ratio", nullable = false, precision = 6, scale = 4)
    private BigDecimal sharpeRatio;

    @Column(name = "calculado_em", nullable = false)
    private LocalDateTime calculadoEm;

    @PrePersist
    protected void onCreate() {
        if (calculadoEm == null) calculadoEm = LocalDateTime.now();
    }

    public enum Volatilidade {
        Baixa, Media, Alta
    }
}
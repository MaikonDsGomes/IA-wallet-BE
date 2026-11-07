package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import com.wallet.md.domain.model.enums.StatusScore;

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

    @Column(name = "score_total")
    private Integer scoreTotal;

    @Column(name = "score_diversificacao")
    private Integer scoreDiversificacao;

    @Column(name = "score_risco")
    private Integer scoreRisco;

    @Column(name = "score_concentracao")
    private Integer scoreConcentracao;

    @Column(name = "score_performance")
    private Integer scorePerformance;

    @Column(name = "num_ativos", nullable = false)
    private Integer numAtivos;

    @Column(name = "num_classes", nullable = false)
    private Integer numClasses;

    @Column(name = "concentracao_maxima", precision = 5, scale = 2)
    private BigDecimal concentracaoMaxima;

    @Column(name = "top3_concentracao", precision = 5, scale = 2)
    private BigDecimal top3Concentracao;

    @Column(name = "indice_herfindahl", precision = 6, scale = 4)
    private BigDecimal indiceHerfindahl;

    @Column(name = "num_setores")
    private Integer numSetores;

    @Enumerated(EnumType.STRING)
    @Column
    private Volatilidade volatilidade;

    @Column(name = "beta_carteira", precision = 6, scale = 4)
    private BigDecimal betaCarteira;

    @Column(name = "var_95", precision = 8, scale = 4)
    private BigDecimal var95;

    @Column(name = "vs_cdi", precision = 8, scale = 4)
    private BigDecimal vsCdi;

    @Column(name = "vs_ibov", precision = 8, scale = 4)
    private BigDecimal vsIbov;

    @Column(name = "sharpe_ratio", precision = 6, scale = 4)
    private BigDecimal sharpeRatio;

    @Column(name = "calculado_em", nullable = false)
    private LocalDateTime calculadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusScore status;

    @Column(name = "versao_metodologia", nullable = false, length = 30)
    private String versaoMetodologia;

    @Column(name = "perfil_risco", length = 20)
    private String perfilRisco;

    @PrePersist
    protected void onCreate() {
        if (calculadoEm == null) calculadoEm = LocalDateTime.now();
        if (status == null) status = StatusScore.PARCIAL;
        if (versaoMetodologia == null) versaoMetodologia = "v1";
    }

    public enum Volatilidade {
        Baixa, Media, Alta
    }
}
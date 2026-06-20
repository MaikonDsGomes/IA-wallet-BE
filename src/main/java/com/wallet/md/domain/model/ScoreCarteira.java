package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.Volatilidade;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCarteira {

    private Long id;
    private Long carteiraId;
    private Integer scoreTotal;
    private Integer scoreDiversificacao;
    private Integer scoreRisco;
    private Integer scoreConcentracao;
    private Integer scorePerformance;
    private Integer numAtivos;
    private Integer numClasses;
    private BigDecimal concentracaoMaxima;
    private BigDecimal top3Concentracao;
    private BigDecimal indiceHerfindahl;
    private Integer numSetores;
    private Volatilidade volatilidade;
    private BigDecimal betaCarteira;
    private BigDecimal var95;
    private BigDecimal vsCdi;
    private BigDecimal vsIbov;
    private BigDecimal sharpeRatio;
    private LocalDateTime calculadoEm;
}

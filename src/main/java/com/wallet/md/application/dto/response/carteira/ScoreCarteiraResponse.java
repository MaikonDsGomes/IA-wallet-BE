package com.wallet.md.application.dto.response.carteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ScoreCarteiraResponse(

        // Geral
        Integer scoreTotal,
        String classificacao, // "Bom", "Ótimo", "Regular"...

        // Diversificação
        Integer scoreDiversificacao,
        Integer numAtivos,
        Integer numClasses,
        BigDecimal concentracaoMaxima,

        // Gestão de Risco
        Integer scoreRisco,
        String volatilidade,
        BigDecimal betaCarteira,
        BigDecimal var95,

        // Concentração
        Integer scoreConcentracao,
        BigDecimal top3Concentracao,
        BigDecimal indiceHerfindahl,
        Integer numSetores,

        // Performance
        Integer scorePerformance,
        BigDecimal vsCdi,
        BigDecimal vsIbov,
        BigDecimal sharpeRatio,

        LocalDateTime calculadoEm
) {}

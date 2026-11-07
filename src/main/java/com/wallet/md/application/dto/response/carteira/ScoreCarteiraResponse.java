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

        LocalDateTime calculadoEm,
        String status,
        String versaoMetodologia,
        String perfilRisco
) {
    public ScoreCarteiraResponse(
            Integer scoreTotal, String classificacao, Integer scoreDiversificacao,
            Integer numAtivos, Integer numClasses, BigDecimal concentracaoMaxima,
            Integer scoreRisco, String volatilidade, BigDecimal betaCarteira,
            BigDecimal var95, Integer scoreConcentracao, BigDecimal top3Concentracao,
            BigDecimal indiceHerfindahl, Integer numSetores, Integer scorePerformance,
            BigDecimal vsCdi, BigDecimal vsIbov, BigDecimal sharpeRatio,
            LocalDateTime calculadoEm) {
        this(scoreTotal, classificacao, scoreDiversificacao, numAtivos, numClasses,
                concentracaoMaxima, scoreRisco, volatilidade, betaCarteira, var95,
                scoreConcentracao, top3Concentracao, indiceHerfindahl, numSetores,
                scorePerformance, vsCdi, vsIbov, sharpeRatio, calculadoEm,
                "COMPLETO", "v1", null);
    }

    @com.fasterxml.jackson.annotation.JsonProperty("interpretacaoDiversificacao")
    public String interpretacaoDiversificacao() {
        return scoreDiversificacao == null ? null : scoreDiversificacao >= 80
                ? "Sua carteira está bem distribuída entre ativos e classes."
                : scoreDiversificacao >= 60 ? "A diversificação é adequada, mas pode ser ampliada."
                : "A carteira está concentrada; considere distribuir melhor os investimentos.";
    }
    @com.fasterxml.jackson.annotation.JsonProperty("interpretacaoRisco")
    public String interpretacaoRisco() {
        return scoreRisco == null ? null : scoreRisco >= 80 ? "Risco controlado para a composição atual."
                : scoreRisco >= 60 ? "Risco moderado; acompanhe volatilidade e exposição."
                : "Atenção: o nível de risco da carteira está elevado.";
    }
    @com.fasterxml.jackson.annotation.JsonProperty("interpretacaoConcentracao")
    public String interpretacaoConcentracao() {
        return scoreConcentracao == null ? null : scoreConcentracao >= 80
                ? "A concentração está sob controle." : "Atenção: há concentração relevante em poucos ativos.";
    }
    @com.fasterxml.jackson.annotation.JsonProperty("interpretacaoPerformance")
    public String interpretacaoPerformance() {
        return scorePerformance == null ? null : scorePerformance >= 80
                ? "Desempenho acima dos principais referenciais." : "O desempenho pode ser acompanhado e melhorado.";
    }
}

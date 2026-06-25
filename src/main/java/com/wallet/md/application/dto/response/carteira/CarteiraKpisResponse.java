package com.wallet.md.application.dto.response.carteira;

import java.math.BigDecimal;
import java.util.Map;

public record CarteiraKpisResponse(
        BigDecimal valorTotal,           // preço atual × quantidade de todas as posições
        BigDecimal valorInvestido,       // preço médio × quantidade de todas as posições
        BigDecimal lucroAcumulado,       // valorTotal - valorInvestido
        BigDecimal rentabilidadeMes,     // % de variação desde o início do mês
        Map<String, BigDecimal> distribuicaoPorClasse  // ex: {"ACAO": 45.5, "FII": 30.0, ...}
) {}
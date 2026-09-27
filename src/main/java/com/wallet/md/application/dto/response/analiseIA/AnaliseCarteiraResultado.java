package com.wallet.md.application.dto.response.analiseIA;

import java.util.List;

public record AnaliseCarteiraResultado(
        Integer score,
        String resumo,
        List<RecomendacaoIA> recomendacoes
) {}

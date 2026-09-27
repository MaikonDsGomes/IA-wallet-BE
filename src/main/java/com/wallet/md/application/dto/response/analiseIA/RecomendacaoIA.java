package com.wallet.md.application.dto.response.analiseIA;

public record RecomendacaoIA(
        String tipo,
        String titulo,
        String descricao,
        String nivel,
        String categoria,
        String ticker,
        String acao
) {}
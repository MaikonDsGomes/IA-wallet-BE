package com.wallet.md.application.dto.response.carteira;

import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.Prioridade;

import java.time.LocalDateTime;

public record RecomendacaoResponse(
        Long id,
        Long ativoId,
        String titulo,
        String descricao,
        CategoriaRecomendacao categoria,
        Prioridade prioridade,
        Integer impactoPts,
        LocalDateTime geradaEm
) {
}

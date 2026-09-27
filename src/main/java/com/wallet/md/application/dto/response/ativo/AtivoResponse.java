package com.wallet.md.application.dto.response.ativo;

import com.wallet.md.domain.model.enums.TipoAtivo;

import java.math.BigDecimal;

public record AtivoResponse(
        Long id,
        String nome,
        String codigo,
        TipoAtivo tipo,
        Integer volatilidade,
        BigDecimal precoAtual,
        BigDecimal variacaoAtual
) {
}

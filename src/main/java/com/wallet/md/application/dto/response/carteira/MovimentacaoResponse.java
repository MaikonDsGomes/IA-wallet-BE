package com.wallet.md.application.dto.response.carteira;

import com.wallet.md.domain.model.enums.TipoMovimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        TipoMovimentacao tipo,
        BigDecimal valor,
        LocalDateTime data,
        BigDecimal saldoApos
) {
}

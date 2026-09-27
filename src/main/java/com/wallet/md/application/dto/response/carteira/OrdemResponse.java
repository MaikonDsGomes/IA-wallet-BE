package com.wallet.md.application.dto.response.carteira;

import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoOrdem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemResponse(
        Long id,
        Long ativoId,
        TipoOrdem tipo,
        Integer quantidade,
        BigDecimal precoExecucao,
        StatusOrdem status,
        LocalDateTime criadaEm
) {
}

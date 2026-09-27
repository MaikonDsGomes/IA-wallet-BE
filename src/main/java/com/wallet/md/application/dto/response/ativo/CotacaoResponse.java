package com.wallet.md.application.dto.response.ativo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CotacaoResponse(
        Long id,
        BigDecimal preco,
        BigDecimal variacao,
        LocalDateTime coletadoEm
) {
}

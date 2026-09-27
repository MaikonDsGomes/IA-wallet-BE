package com.wallet.md.application.dto.response.carteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraPosicaoResponse(
        Long ordemId,
        Long ativoId,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorBruto,
        BigDecimal saldoAtual,
        LocalDateTime executadaEm
) {
}

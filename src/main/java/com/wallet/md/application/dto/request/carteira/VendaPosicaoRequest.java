package com.wallet.md.application.dto.request.carteira;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VendaPosicaoRequest(
        @NotNull(message = "Ativo é obrigatório")
        Long ativoId,
        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantidade,
        @NotNull(message = "Preço unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
        BigDecimal precoUnitario
) {
}

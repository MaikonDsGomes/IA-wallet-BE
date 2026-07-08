package com.wallet.md.application.dto.request.carteira;

import com.wallet.md.domain.model.enums.TipoAtivo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PostPosicaoRequest(

        @NotBlank(message = "Código do ativo é obrigatório")
        String codigo,

        @NotNull(message = "Tipo do ativo é obrigatório")
        TipoAtivo tipo,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantidade,

        @NotNull(message = "Preço médio é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço médio deve ser maior que zero")
        BigDecimal precoMedio

) {}
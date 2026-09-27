package com.wallet.md.application.dto.request.analiseIA;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtivoCarteiraRequest(

        @NotBlank
        String ticker,

        @NotBlank
        String nome,

        @NotBlank
        String setor,

        @NotNull
        BigDecimal percentual
) {}
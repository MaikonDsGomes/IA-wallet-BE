package com.wallet.md.application.dto.request.analiseIA;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlocacaoRequest(

        @NotNull
        @JsonProperty("renda_fixa")
        BigDecimal rendaFixa,

        @NotNull
        BigDecimal acoes,

        @NotNull
        BigDecimal fii,

        @NotNull
        BigDecimal etf
) {}
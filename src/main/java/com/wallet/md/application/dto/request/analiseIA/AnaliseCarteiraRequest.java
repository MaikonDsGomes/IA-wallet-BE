package com.wallet.md.application.dto.request.analiseIA;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record AnaliseCarteiraRequest(

        @NotBlank
        String perfil,

        @NotNull
        @JsonProperty("valor_total")
        BigDecimal valorTotal,

        @NotNull
        BigDecimal rentabilidade,

        @NotNull
        @Valid
        AlocacaoRequest alocacao,

        @NotEmpty
        @Valid
        List<AtivoCarteiraRequest> ativos
) {}
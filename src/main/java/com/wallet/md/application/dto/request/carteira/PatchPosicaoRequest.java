package com.wallet.md.application.dto.request.carteira;

import jakarta.validation.constraints.NotNull;

public record PatchPosicaoRequest(
        @NotNull(message = "O ID do ativo é obrigatório")
        Long ativoId
){}

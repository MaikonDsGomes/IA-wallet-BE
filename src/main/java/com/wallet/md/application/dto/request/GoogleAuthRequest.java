package com.wallet.md.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank(message = "Credential do Google é obrigatória")
        String credential
) {}

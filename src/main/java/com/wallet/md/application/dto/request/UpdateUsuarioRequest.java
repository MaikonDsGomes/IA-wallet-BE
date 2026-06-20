package com.wallet.md.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUsuarioRequest(
        @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
        String nome,

        @Email(message = "Email deve ser válido")
        @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
        String email
) {}

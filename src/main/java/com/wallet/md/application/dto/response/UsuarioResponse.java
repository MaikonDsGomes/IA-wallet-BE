package com.wallet.md.application.dto.response;

import com.wallet.md.domain.model.enums.Role;

import java.time.LocalDateTime;

public record UsuarioResponse(
        String nome,
        String email,
        Role role,
        Boolean status,
        LocalDateTime criado
) {}

package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.Role;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Boolean status;
    private Role role;
    private LocalDateTime criado;
}

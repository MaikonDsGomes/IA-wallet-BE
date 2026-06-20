package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 1000)
    private String senha;

    @Column(nullable = false)
    private Boolean status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criado;

    @PrePersist
    protected void onCreate() {
        if (criado == null) criado = LocalDateTime.now();
        if (status == null) status = true;
        if (role == null) role = Role.USER;
    }
}

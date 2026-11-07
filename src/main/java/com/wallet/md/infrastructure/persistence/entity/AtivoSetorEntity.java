package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ativo_setor",
        uniqueConstraints = @UniqueConstraint(name = "uk_ativo_setor_ativo", columnNames = "ativo_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoSetorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id", nullable = false)
    private AtivoEntity ativo;

    @Column(nullable = false, length = 150)
    private String setor;

    @Column(nullable = false, length = 40)
    private String fonte;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}

package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ativo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAtivo tipo;

    @Column(nullable = false)
    private Integer volatilidade;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    protected void onCreate() {
        if (volatilidade == null) volatilidade = 0;
        if (ativo == null) ativo = true;
    }
}

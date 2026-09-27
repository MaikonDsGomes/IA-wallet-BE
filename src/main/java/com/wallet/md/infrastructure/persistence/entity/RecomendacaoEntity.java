package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.Prioridade;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private CarteiraEntity carteira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id")
    private AtivoEntity ativo;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaRecomendacao categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Column(name = "impacto_pts", nullable = false)
    private Integer impactoPts;

    @Column(nullable = false)
    private Boolean ativa;

    @Column(name = "gerada_em", nullable = false)
    private LocalDateTime geradaEm;

    @PrePersist
    protected void onCreate() {
        if (impactoPts == null) impactoPts = 0;
        if (ativa == null) ativa = true;
        if (geradaEm == null) geradaEm = LocalDateTime.now();
    }
}

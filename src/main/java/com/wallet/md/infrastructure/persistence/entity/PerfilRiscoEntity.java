package com.wallet.md.infrastructure.persistence.entity;

import com.wallet.md.domain.model.enums.NivelRisco;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "perfil_risco")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRiscoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRisco nivel;

    @Column(name = "margem_minima", nullable = false)
    private Integer margemMinima;

    @Column(name = "calculado_em", nullable = false)
    private LocalDateTime calculadoEm;

    @PrePersist
    protected void onCreate() {
        if (score == null) score = 0;
        if (nivel == null) nivel = NivelRisco.MODERADO;
        if (margemMinima == null) margemMinima = 0;
        if (calculadoEm == null) calculadoEm = LocalDateTime.now();
    }
}

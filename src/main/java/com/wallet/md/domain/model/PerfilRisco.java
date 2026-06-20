package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.NivelRisco;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRisco {

    private Long id;
    private Long usuarioId;
    private Integer score;
    private NivelRisco nivel;
    private Integer margemMinima;
    private LocalDateTime calculadoEm;
}

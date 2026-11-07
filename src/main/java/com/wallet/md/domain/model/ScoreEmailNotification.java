package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.StatusScoreEmailNotification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreEmailNotification {
    private Long id;
    private Long recalculoId;
    private StatusScoreEmailNotification status;
    private Integer tentativas;
    private String ultimoErro;
    private LocalDateTime enviadoEm;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

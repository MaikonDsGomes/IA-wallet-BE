package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.StatusRecalculoScore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculoScore {

    private Long id;
    private Long carteiraId;
    private LocalDateTime agendadoPara;
    private StatusRecalculoScore status;
    private Integer tentativas;
    private Integer versaoAlteracao;
    private String ultimoErro;
    private LocalDateTime processadoEm;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private Long version;
}

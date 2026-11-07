package com.wallet.md.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoSetor {
    private Long ativoId;
    private String setor;
    private String fonte;
    private LocalDateTime atualizadoEm;
}

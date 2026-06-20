package com.wallet.md.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiscoAlerta {

    private Long id;
    private Long contaId;
    private Integer nivel;
    private String mensagem;
    private LocalDateTime criadoEm;
}

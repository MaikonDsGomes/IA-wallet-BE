package com.wallet.md.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posicao {

    private Long id;
    private Long carteiraId;
    private Long ativoId;
    private Integer quantidade;
    private BigDecimal precoMedio;
}

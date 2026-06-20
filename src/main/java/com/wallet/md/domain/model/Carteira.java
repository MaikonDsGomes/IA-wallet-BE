package com.wallet.md.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {

    private Long id;
    private Long contaId;
    private BigDecimal valorTotal;
    private BigDecimal rentabilidadeTotal;
    private BigDecimal rentabilidadeMes;
    private LocalDateTime dataUltimoSnapshot;
}

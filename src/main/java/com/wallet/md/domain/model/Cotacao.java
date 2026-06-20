package com.wallet.md.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cotacao {

    private Long id;
    private Long ativoId;
    private BigDecimal preco;
    private BigDecimal variacao;
    private LocalDateTime coletadoEm;
}

package com.wallet.md.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    private Long id;
    private Long usuarioId;
    private BigDecimal saldo;
    private Integer margemDisponivel;
}

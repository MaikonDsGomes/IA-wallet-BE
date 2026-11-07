package com.wallet.md.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPoint {
    private String instrumento;
    private String tipo;
    private LocalDate data;
    private BigDecimal valor;
    private BigDecimal valorAjustado;
    private String fonte;
}

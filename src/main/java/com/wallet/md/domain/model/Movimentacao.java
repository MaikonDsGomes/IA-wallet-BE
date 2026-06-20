package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.TipoMovimentacao;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {

    private Long id;
    private Long contaId;
    private Long contaDestinoId;
    private TipoMovimentacao tipo;
    private BigDecimal valor;
    private LocalDateTime data;
}

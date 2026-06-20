package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoOrdem;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ordem {

    private Long id;
    private Long contaId;
    private Long ativoId;
    private TipoOrdem tipo;
    private Integer quantidade;
    private BigDecimal precoExecucao;
    private StatusOrdem status;
    private LocalDateTime criadaEm;
}

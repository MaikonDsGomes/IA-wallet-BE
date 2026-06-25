package com.wallet.md.infrastructure.persistence.projection;

import com.wallet.md.domain.model.enums.TipoAtivo;
import java.math.BigDecimal;

public interface PosicaoDetalhadaProjection {
    Long getAtivoId();
    String getAtivoNome();
    TipoAtivo getAtivoTipo();
    Integer getQuantidade();
    BigDecimal getPrecoMedio();
}
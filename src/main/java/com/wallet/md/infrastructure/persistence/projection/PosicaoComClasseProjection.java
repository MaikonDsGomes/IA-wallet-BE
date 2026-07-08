package com.wallet.md.infrastructure.persistence.projection;

import java.math.BigDecimal;

// infrastructure/persistence/projection/PosicaoComClasseProjection.java
public interface PosicaoComClasseProjection {
    Long getId();
    Long getCarteiraId();
    Long getAtivoId();
    Integer getQuantidade();
    BigDecimal getPrecoMedio();
    String getAtivoClasse();  // vem do JOIN com ativo
}
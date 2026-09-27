package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Conta;
import java.util.Optional;

public interface ContaRepository {
    Optional<Conta> findByUsuarioId(Long usuarioId);

    void creditar(Long contaId, java.math.BigDecimal valor);

    boolean debitar(Long contaId, java.math.BigDecimal valor);
}

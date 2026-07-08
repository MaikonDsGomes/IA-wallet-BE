package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Conta;
import java.util.Optional;

public interface ContaRepository {
    Optional<Conta> findByUsuarioId(Long usuarioId);
}

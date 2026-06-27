package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Ativo;

import java.util.Optional;

public interface AtivoRepository {
    Optional<Ativo> findByCodigo(String codigo);
}

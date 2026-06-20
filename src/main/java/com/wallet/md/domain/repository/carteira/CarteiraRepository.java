package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Carteira;

import java.util.Optional;

public interface CarteiraRepository {
    Optional<Carteira> findByContaId(Long contaId);
}

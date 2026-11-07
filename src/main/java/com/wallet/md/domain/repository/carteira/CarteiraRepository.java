package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Carteira;


import java.util.Optional;
import java.util.List;

public interface CarteiraRepository {
    Optional<Carteira> findByContaId(Long contaId);

    Carteira createIfAbsent(Long contaId);

    Optional<Carteira> findCarteiraIdByUsuarioId(Long usuarioId);

    Optional<Long> findUsuarioIdByCarteiraId(Long carteiraId);

    List<Long> findUsuarioIds();
}

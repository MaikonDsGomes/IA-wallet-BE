package com.wallet.md.domain.repository.carteira;

import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;

import java.util.List;

public interface PosicaoRepository {
    List<PosicaoComClasseProjection> findByCarteiraId(Long carteiraId);
}
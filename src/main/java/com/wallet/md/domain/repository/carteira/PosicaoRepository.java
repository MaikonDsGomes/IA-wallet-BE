package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;

import java.util.List;

public interface PosicaoRepository {
    List<PosicaoComClasseProjection> findByCarteiraId(Long carteiraId);

    List<PosicaoDetalhadaProjection> findDetalhadaByCarteiraId(Long carteiraId, TipoAtivo tipo);
}
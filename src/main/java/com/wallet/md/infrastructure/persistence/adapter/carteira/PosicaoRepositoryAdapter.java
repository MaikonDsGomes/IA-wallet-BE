package com.wallet.md.infrastructure.persistence.adapter.carteira;

import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;
import com.wallet.md.infrastructure.persistence.repository.JpaCarteiraRepository;
import com.wallet.md.infrastructure.persistence.repository.carteira.JpaPosicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PosicaoRepositoryAdapter implements PosicaoRepository {

    private final JpaPosicaoRepository jpaRepository;

    @Override
    public List<PosicaoComClasseProjection> findByCarteiraId(Long carteiraId) {
        return jpaRepository.findByCarteiraId(carteiraId);
    }

    @Override
    public List<PosicaoDetalhadaProjection> findDetalhadaByCarteiraId(Long carteiraId, TipoAtivo tipo) {
        return jpaRepository.findDetalhadaByCarteiraId(carteiraId, tipo);
    }
}

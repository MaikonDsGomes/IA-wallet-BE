package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Posicao;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface PosicaoRepository {
    List<PosicaoComClasseProjection> findByCarteiraId(Long carteiraId);

    List<PosicaoDetalhadaProjection> findDetalhadaByCarteiraId(Long carteiraId, TipoAtivo tipo);

    void save(Long carteiraId, Long ativoId, Integer quantidade, BigDecimal precoMedio);

    Optional<Posicao> findByCarteiraIdAndAtivoId(Long carteiraId, Long ativoId);

    void deleteByCarteiraIdAndAtivoId(Long carteiraId, Long ativoId);

    void updateQuantidadeVenda(Long carteiraId, Long ativoId, Integer quantidadeVendida, BigDecimal precoVenda);
}
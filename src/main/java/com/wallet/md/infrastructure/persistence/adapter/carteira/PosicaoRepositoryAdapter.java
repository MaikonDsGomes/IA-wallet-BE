package com.wallet.md.infrastructure.persistence.adapter.carteira;

import com.wallet.md.domain.model.Posicao;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import com.wallet.md.infrastructure.persistence.entity.PosicaoEntity;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;
import com.wallet.md.infrastructure.persistence.repository.JpaCarteiraRepository;
import com.wallet.md.infrastructure.persistence.repository.carteira.JpaPosicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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


    @Override
    public Optional<Posicao> findByCarteiraIdAndAtivoId(Long carteiraId, Long ativoId) {
        return jpaRepository.findByCarteiraIdAndAtivoId(carteiraId, ativoId)
                .map(this::toDomain);
    }

    @Override
    public void save(Long carteiraId, Long ativoId, Integer quantidade, BigDecimal precoMedio) {
        jpaRepository.findByCarteiraIdAndAtivoId(carteiraId, ativoId).ifPresentOrElse(
                existing -> {
                    // Recalcula preço médio ponderado
                    int totalQtd = existing.getQuantidade() + quantidade;
                    BigDecimal novoPrecoMedio = existing.getPrecoMedio()
                            .multiply(BigDecimal.valueOf(existing.getQuantidade()))
                            .add(precoMedio.multiply(BigDecimal.valueOf(quantidade)))
                            .divide(BigDecimal.valueOf(totalQtd), 4, RoundingMode.HALF_UP);
                    existing.setQuantidade(totalQtd);
                    existing.setPrecoMedio(novoPrecoMedio);
                    jpaRepository.save(existing);
                },
                () -> {
                    CarteiraEntity carteira = new CarteiraEntity();
                    carteira.setId(carteiraId);
                    AtivoEntity ativo = new AtivoEntity();
                    ativo.setId(ativoId);
                    PosicaoEntity nova = PosicaoEntity.builder()
                            .carteira(carteira)
                            .ativo(ativo)
                            .quantidade(quantidade)
                            .precoMedio(precoMedio)
                            .build();
                    jpaRepository.save(nova);
                }
        );
    }

    private Posicao toDomain(PosicaoEntity entity) {
        return Posicao.builder()
                .id(entity.getId())
                .carteiraId(entity.getCarteira().getId())
                .ativoId(entity.getAtivo().getId())
                .quantidade(entity.getQuantidade())
                .precoMedio(entity.getPrecoMedio())
                .build();
    }
}

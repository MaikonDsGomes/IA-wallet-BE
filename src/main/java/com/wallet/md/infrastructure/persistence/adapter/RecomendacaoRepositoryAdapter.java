package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.Recomendacao;
import com.wallet.md.domain.repository.carteira.RecomendacaoRepository;
import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import com.wallet.md.infrastructure.persistence.entity.RecomendacaoEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaRecomendacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecomendacaoRepositoryAdapter implements RecomendacaoRepository {

    private final JpaRecomendacaoRepository jpaRepository;

    @Override
    public List<Recomendacao> findAtivasByCarteiraId(Long carteiraId) {
        return jpaRepository.findByCarteiraIdAndAtivaTrueOrderByPrioridadeAscGeradaEmDesc(carteiraId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void substituirAtivas(Long carteiraId, List<Recomendacao> recomendacoes) {
        jpaRepository.desativarAtivas(carteiraId);
        var carteira = new CarteiraEntity();
        carteira.setId(carteiraId);

        var entities = recomendacoes.stream()
                .map(recomendacao -> toEntity(recomendacao, carteira))
                .toList();
        jpaRepository.saveAll(entities);
    }

    private Recomendacao toDomain(RecomendacaoEntity entity) {
        return Recomendacao.builder()
                .id(entity.getId())
                .carteiraId(entity.getCarteira().getId())
                .ativoId(entity.getAtivo() == null ? null : entity.getAtivo().getId())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .categoria(entity.getCategoria())
                .prioridade(entity.getPrioridade())
                .impactoPts(entity.getImpactoPts())
                .ativa(entity.getAtiva())
                .geradaEm(entity.getGeradaEm())
                .build();
    }

    private RecomendacaoEntity toEntity(Recomendacao recomendacao, CarteiraEntity carteira) {
        return RecomendacaoEntity.builder()
                .carteira(carteira)
                .titulo(recomendacao.getTitulo())
                .descricao(recomendacao.getDescricao())
                .categoria(recomendacao.getCategoria())
                .prioridade(recomendacao.getPrioridade())
                .impactoPts(recomendacao.getImpactoPts())
                .ativa(recomendacao.getAtiva())
                .geradaEm(recomendacao.getGeradaEm())
                .build();
    }
}

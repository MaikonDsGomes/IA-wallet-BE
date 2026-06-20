package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.Volatilidade;
import com.wallet.md.domain.repository.carteira.ScoreCarteiraRepository;
import com.wallet.md.infrastructure.persistence.entity.ScoreCarteiraEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaScoreCarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScoreCarteiraRepositoryAdapter implements ScoreCarteiraRepository {

    private final JpaScoreCarteiraRepository jpaRepository;

    @Override
    public Optional<ScoreCarteira> findLatestByCarteiraId(Long carteiraId) {
        return jpaRepository.findTopByCarteiraIdOrderByCalculadoEmDesc(carteiraId)
                .map(this::toDomain);
    }

    private ScoreCarteira toDomain(ScoreCarteiraEntity entity) {
        return ScoreCarteira.builder()
                .id(entity.getId())
                .carteiraId(entity.getCarteira().getId())
                .scoreTotal(entity.getScoreTotal())
                .scoreDiversificacao(entity.getScoreDiversificacao())
                .scoreRisco(entity.getScoreRisco())
                .scoreConcentracao(entity.getScoreConcentracao())
                .scorePerformance(entity.getScorePerformance())
                .numAtivos(entity.getNumAtivos())
                .numClasses(entity.getNumClasses())
                .concentracaoMaxima(entity.getConcentracaoMaxima())
                .top3Concentracao(entity.getTop3Concentracao())
                .indiceHerfindahl(entity.getIndiceHerfindahl())
                .numSetores(entity.getNumSetores())
                .volatilidade(Volatilidade.valueOf(entity.getVolatilidade().name()))
                .betaCarteira(entity.getBetaCarteira())
                .var95(entity.getVar95())
                .vsCdi(entity.getVsCdi())
                .vsIbov(entity.getVsIbov())
                .sharpeRatio(entity.getSharpeRatio())
                .calculadoEm(entity.getCalculadoEm())
                .build();
    }
}
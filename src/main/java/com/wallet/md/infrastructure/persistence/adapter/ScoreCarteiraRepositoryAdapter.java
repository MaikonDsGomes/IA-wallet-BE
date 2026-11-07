package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.Volatilidade;
import com.wallet.md.domain.repository.carteira.ScoreCarteiraRepository;
import com.wallet.md.infrastructure.persistence.entity.ScoreCarteiraEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaScoreCarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;

@Repository
@RequiredArgsConstructor
public class ScoreCarteiraRepositoryAdapter implements ScoreCarteiraRepository {

    private final JpaScoreCarteiraRepository jpaRepository;

    @Override
    public Optional<ScoreCarteira> findLatestByCarteiraId(Long carteiraId) {
        return jpaRepository.findTopByCarteiraIdOrderByCalculadoEmDesc(carteiraId)
                .map(this::toDomain);
    }

    @Override
    public ScoreCarteira save(ScoreCarteira score) {
        var carteira = new CarteiraEntity();
        carteira.setId(score.getCarteiraId());
        var entity = ScoreCarteiraEntity.builder()
                .id(score.getId())
                .carteira(carteira)
                .scoreTotal(score.getScoreTotal())
                .scoreDiversificacao(score.getScoreDiversificacao())
                .scoreRisco(score.getScoreRisco())
                .scoreConcentracao(score.getScoreConcentracao())
                .scorePerformance(score.getScorePerformance())
                .numAtivos(score.getNumAtivos())
                .numClasses(score.getNumClasses())
                .concentracaoMaxima(score.getConcentracaoMaxima())
                .top3Concentracao(score.getTop3Concentracao())
                .indiceHerfindahl(score.getIndiceHerfindahl())
                .numSetores(score.getNumSetores())
                .volatilidade(score.getVolatilidade() == null ? null
                        : ScoreCarteiraEntity.Volatilidade.valueOf(score.getVolatilidade().name()))
                .betaCarteira(score.getBetaCarteira())
                .var95(score.getVar95())
                .vsCdi(score.getVsCdi())
                .vsIbov(score.getVsIbov())
                .sharpeRatio(score.getSharpeRatio())
                .calculadoEm(score.getCalculadoEm())
                .status(score.getStatus())
                .versaoMetodologia(score.getVersaoMetodologia())
                .perfilRisco(score.getPerfilRisco())
                .build();
        return toDomain(jpaRepository.save(entity));
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
                .status(entity.getStatus())
                .versaoMetodologia(entity.getVersaoMetodologia())
                .perfilRisco(entity.getPerfilRisco())
                .build();
    }
}
package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.ScoreCarteiraResponse;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.PerfilRiscoRepository;
import com.wallet.md.domain.repository.carteira.ScoreCarteiraRepository;
import com.wallet.md.infrastructure.config.ScoreMetodologiaProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetScoreCarteiraUseCase {


    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final ScoreCarteiraRepository scoreCarteiraRepository;
    private final ScoreMetodologiaProperties properties;

    public GetScoreCarteiraUseCase(ContaRepository contaRepository, CarteiraRepository carteiraRepository,
                                   ScoreCarteiraRepository scoreCarteiraRepository) {
        this(contaRepository, carteiraRepository, scoreCarteiraRepository, new ScoreMetodologiaProperties());
    }

    public ScoreCarteiraResponse getScore(Long usuarioId) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta", usuarioId));

        var carteira = carteiraRepository.findByContaId(conta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira", conta.getId()));

        var score = scoreCarteiraRepository.findLatestByCarteiraId(carteira.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Score", carteira.getId()));

        return new ScoreCarteiraResponse(
                score.getScoreTotal(),
                resolverClassificacao(score.getScoreTotal()),
                score.getScoreDiversificacao(),
                score.getNumAtivos(),
                score.getNumClasses(),
                score.getConcentracaoMaxima(),
                score.getScoreRisco(),
                score.getVolatilidade() == null ? null : score.getVolatilidade().name(),
                score.getBetaCarteira(),
                score.getVar95(),
                score.getScoreConcentracao(),
                score.getTop3Concentracao(),
                score.getIndiceHerfindahl(),
                score.getNumSetores(),
                score.getScorePerformance(),
                score.getVsCdi(),
                score.getVsIbov(),
                score.getSharpeRatio(),
                score.getCalculadoEm(),
                score.getStatus() == null ? null : score.getStatus().name(),
                score.getVersaoMetodologia(),
                score.getPerfilRisco()
        );
    }

    private String resolverClassificacao(Integer score) {
        if (score == null) return "Pendente";
        var classificacao = properties.getClassificacao();
        if (score > classificacao.getMuitoBomMax()) return "Excelente";
        if (score > classificacao.getBomMax()) return "Muito bom";
        if (score > classificacao.getAtencaoMax()) return "Bom";
        if (score > classificacao.getCriticoMax()) return "Atenção";
        return "Crítico";
    }
}
package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.ScoreCarteiraResponse;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.PerfilRiscoRepository;
import com.wallet.md.domain.repository.carteira.ScoreCarteiraRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetScoreCarteiraUseCase {


    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final ScoreCarteiraRepository scoreCarteiraRepository;

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
                score.getVolatilidade().name(),
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
                score.getCalculadoEm()
        );
    }

    private String resolverClassificacao(Integer score) {
        if (score >= 90) return "Excelente";
        if (score >= 75) return "Bom";
        if (score >= 60) return "Regular";
        if (score >= 40) return "Atenção";
        return "Crítico";
    }
}
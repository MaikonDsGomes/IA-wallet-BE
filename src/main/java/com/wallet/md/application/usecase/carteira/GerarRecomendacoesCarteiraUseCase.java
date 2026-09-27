package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.model.PerfilRisco;
import com.wallet.md.domain.model.Recomendacao;
import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.NivelRisco;
import com.wallet.md.domain.model.enums.Prioridade;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.PerfilRiscoRepository;
import com.wallet.md.domain.repository.carteira.RecomendacaoRepository;
import com.wallet.md.domain.repository.carteira.ScoreCarteiraRepository;
import com.wallet.md.application.service.RecommendationTextProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class GerarRecomendacoesCarteiraUseCase {

    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final PerfilRiscoRepository perfilRiscoRepository;
    private final ScoreCarteiraRepository scoreCarteiraRepository;
    private final RecomendacaoRepository recomendacaoRepository;
    private final RecommendationTextProvider recommendationTextProvider;

    public void gerar(Long usuarioId) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta", usuarioId));
        var carteira = carteiraRepository.findByContaId(conta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira", conta.getId()));
        var score = scoreCarteiraRepository.findLatestByCarteiraId(carteira.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Score", carteira.getId()));
        var perfil = perfilRiscoRepository.findByUsuarioId(usuarioId).orElse(null);

        var recomendacoes = criarRecomendacoes(carteira.getId(), score, perfil).stream()
                .map(recomendacao -> melhorarTexto(recomendacao, score, perfil))
                .toList();
        recomendacaoRepository.substituirAtivas(carteira.getId(), recomendacoes);
    }

    private Recomendacao melhorarTexto(Recomendacao recomendacao, ScoreCarteira score, PerfilRisco perfil) {
        try {
            return recommendationTextProvider.improve(recomendacao, score, perfil);
        } catch (RuntimeException exception) {
            log.warn("Usando texto determinístico após falha do provedor de IA: {}", exception.getMessage());
            return recomendacao;
        }
    }

    private List<Recomendacao> criarRecomendacoes(Long carteiraId, ScoreCarteira score, PerfilRisco perfil) {
        var recomendacoes = new ArrayList<Recomendacao>();

        if (score.getScoreRisco() < 60 || score.getVolatilidade().name().equals("Alta")) {
            recomendacoes.add(recomendacao(carteiraId, "Revise o risco da carteira",
                    "A carteira apresenta risco ou volatilidade elevados para o momento. Avalie reduzir a exposição a posições mais voláteis e manter uma reserva compatível com seus objetivos.",
                    CategoriaRecomendacao.RISCO, Prioridade.ALTA, -10));
        }

        if (score.getScoreConcentracao() < 60 || score.getConcentracaoMaxima().doubleValue() > 40) {
            recomendacoes.add(recomendacao(carteiraId, "Reduza a concentração",
                    "Uma parcela relevante da carteira está concentrada em poucas posições. A diversificação deve ser avaliada de acordo com seu perfil e horizonte de investimento.",
                    CategoriaRecomendacao.RISCO, Prioridade.ALTA, -8));
        }

        if (score.getScoreDiversificacao() < 70 || score.getNumClasses() < 3) {
            recomendacoes.add(recomendacao(carteiraId, "Aumente a diversificação",
                    "A carteira possui pouca diversificação entre classes ou apresenta score de diversificação abaixo do recomendado.",
                    CategoriaRecomendacao.OPORTUNIDADE, Prioridade.MEDIA, 6));
        }

        if (perfil != null && perfil.getNivel() == NivelRisco.CONSERVADOR
                && score.getVolatilidade().name().equals("Alta")) {
            recomendacoes.add(recomendacao(carteiraId, "Alinhe a volatilidade ao seu perfil",
                    "Seu perfil é conservador, mas a volatilidade observada está elevada. Considere revisar a composição com foco na adequação ao seu horizonte e tolerância a perdas.",
                    CategoriaRecomendacao.RISCO, Prioridade.ALTA, -10));
        }

        if (recomendacoes.isEmpty()) {
            recomendacoes.add(recomendacao(carteiraId, "Mantenha o acompanhamento",
                    "Os principais indicadores da carteira estão dentro dos critérios monitorados. Continue acompanhando o score e reavalie sua estratégia quando seus objetivos ou perfil mudarem.",
                    CategoriaRecomendacao.POSITIVO, Prioridade.BAIXA, 3));
        }

        return recomendacoes;
    }

    private Recomendacao recomendacao(Long carteiraId, String titulo, String descricao,
                                      CategoriaRecomendacao categoria, Prioridade prioridade, int impactoPts) {
        return Recomendacao.builder()
                .carteiraId(carteiraId)
                .titulo(titulo)
                .descricao(descricao)
                .categoria(categoria)
                .prioridade(prioridade)
                .impactoPts(impactoPts)
                .ativa(true)
                .build();
    }
}

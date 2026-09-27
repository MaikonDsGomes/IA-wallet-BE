package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.model.Conta;
import com.wallet.md.domain.model.PerfilRisco;
import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.NivelRisco;
import com.wallet.md.domain.model.enums.Volatilidade;
import com.wallet.md.domain.repository.carteira.*;
import com.wallet.md.application.service.RecommendationTextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GerarRecomendacoesCarteiraUseCaseTest {

    private final ContaRepository contaRepository = mock(ContaRepository.class);
    private final CarteiraRepository carteiraRepository = mock(CarteiraRepository.class);
    private final PerfilRiscoRepository perfilRepository = mock(PerfilRiscoRepository.class);
    private final ScoreCarteiraRepository scoreRepository = mock(ScoreCarteiraRepository.class);
    private final RecomendacaoRepository recomendacaoRepository = mock(RecomendacaoRepository.class);
    private final RecommendationTextProvider textProvider = mock(RecommendationTextProvider.class);
    private GerarRecomendacoesCarteiraUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GerarRecomendacoesCarteiraUseCase(
                contaRepository, carteiraRepository, perfilRepository, scoreRepository, recomendacaoRepository,
                textProvider);
    }

    @Test
    void deveGerarRecomendacaoDeConcentracao() {
        givenCarteira();
        whenScore(45, 50, 55, 80, Volatilidade.Media);

        useCase.gerar(1L);

        verify(recomendacaoRepository).substituirAtivas(eq(10L), anyList());
    }

    @Test
    void devePersistirTextoPersonalizadoPeloProvedor() {
        givenCarteira();
        whenScore(45, 50, 55, 80, Volatilidade.Media);
        when(textProvider.improve(any(), any(), any())).thenAnswer(invocation -> {
            var original = invocation.getArgument(0, com.wallet.md.domain.model.Recomendacao.class);
            return com.wallet.md.domain.model.Recomendacao.builder()
                    .id(original.getId())
                    .carteiraId(original.getCarteiraId())
                    .titulo("Texto personalizado")
                    .descricao(original.getDescricao())
                    .categoria(original.getCategoria())
                    .prioridade(original.getPrioridade())
                    .impactoPts(original.getImpactoPts())
                    .ativa(original.getAtiva())
                    .geradaEm(original.getGeradaEm())
                    .build();
        });

        useCase.gerar(1L);

        verify(textProvider, atLeastOnce()).improve(any(), any(), any());
        verify(recomendacaoRepository).substituirAtivas(eq(10L),
                argThat(recomendacoes -> recomendacoes.stream()
                        .anyMatch(recomendacao -> recomendacao.getTitulo().equals("Texto personalizado"))));
    }

    @Test
    void deveUsarTextoDeterministicoQuandoProvedorFalhar() {
        givenCarteira();
        whenScore(45, 50, 55, 80, Volatilidade.Media);
        when(textProvider.improve(any(), any(), any())).thenThrow(new RuntimeException("indisponível"));

        useCase.gerar(1L);

        verify(recomendacaoRepository).substituirAtivas(eq(10L),
                argThat(recomendacoes -> recomendacoes.stream()
                        .anyMatch(recomendacao -> recomendacao.getTitulo().equals("Revise o risco da carteira"))));
    }

    @Test
    void deveGerarRecomendacaoDeAdequacaoAoPerfil() {
        givenCarteira();
        whenScore(90, 90, 90, 90, Volatilidade.Alta);
        when(perfilRepository.findByUsuarioId(1L)).thenReturn(Optional.of(PerfilRisco.builder()
                .usuarioId(1L).nivel(NivelRisco.CONSERVADOR).build()));

        useCase.gerar(1L);

        verify(recomendacaoRepository).substituirAtivas(eq(10L), anyList());
    }

    private void givenCarteira() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(Conta.builder().id(20L).build()));
        when(carteiraRepository.findByContaId(20L)).thenReturn(Optional.of(Carteira.builder().id(10L).build()));
    }

    private void whenScore(int risco, int concentracao, int diversificacao, int performance,
                           Volatilidade volatilidade) {
        when(scoreRepository.findLatestByCarteiraId(10L)).thenReturn(Optional.of(ScoreCarteira.builder()
                .carteiraId(10L)
                .scoreRisco(risco)
                .scoreConcentracao(concentracao)
                .scoreDiversificacao(diversificacao)
                .scorePerformance(performance)
                .numClasses(4)
                .concentracaoMaxima(new BigDecimal("45"))
                .volatilidade(volatilidade)
                .build()));
        when(perfilRepository.findByUsuarioId(1L)).thenReturn(Optional.empty());
    }
}

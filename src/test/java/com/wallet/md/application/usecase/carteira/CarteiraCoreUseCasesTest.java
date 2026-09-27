package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.DeletePosicaoRequest;
import com.wallet.md.application.dto.request.carteira.PostPosicaoRequest;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.model.*;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.model.enums.Volatilidade;
import com.wallet.md.domain.repository.carteira.*;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarteiraCoreUseCasesTest {

    @Mock CarteiraRepository carteiraRepository;
    @Mock AtivoRepository ativoRepository;
    @Mock PosicaoRepository posicaoRepository;
    @Mock ContaRepository contaRepository;
    @Mock ScoreCarteiraRepository scoreRepository;
    @Mock CotacaoRepository cotacaoRepository;

    private Carteira carteira() {
        return Carteira.builder().id(10L).contaId(20L).build();
    }

    private Ativo ativo() {
        return Ativo.builder().id(30L).codigo("PETR4").tipo(TipoAtivo.ACAO).ativo(true).build();
    }

    @Test
    void deveAdicionarPosicao() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(ativoRepository.findByCodigo("PETR4")).willReturn(Optional.of(ativo()));

        new PostPosicaoUseCase(carteiraRepository, ativoRepository, posicaoRepository)
                .post(1L, new PostPosicaoRequest("PETR4", TipoAtivo.ACAO, 10, new BigDecimal("32.40")));

        verify(posicaoRepository).save(10L, 30L, 10, new BigDecimal("32.40"));
    }

    @Test
    void deveRecusarTipoDivergenteAoAdicionarPosicao() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(ativoRepository.findByCodigo("PETR4")).willReturn(Optional.of(ativo()));

        assertThatThrownBy(() -> new PostPosicaoUseCase(carteiraRepository, ativoRepository, posicaoRepository)
                .post(1L, new PostPosicaoRequest("PETR4", TipoAtivo.FII, 10, new BigDecimal("32.40"))))
                .isInstanceOf(IllegalArgumentException.class);
        verify(posicaoRepository, never()).save(anyLong(), anyLong(), anyInt(), any());
    }

    @Test
    void deveVenderPosicao() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(ativoRepository.findById(30L)).willReturn(Optional.of(ativo()));
        given(posicaoRepository.findByCarteiraIdAndAtivoId(10L, 30L))
                .willReturn(Optional.of(Posicao.builder().id(40L).build()));

        new PatchPosicaoUseCase(carteiraRepository, ativoRepository, posicaoRepository)
                .vendaAtivo(1L, 30L, 2, new BigDecimal("35.00"));

        verify(posicaoRepository).updateQuantidadeVenda(10L, 30L, 2, new BigDecimal("35.00"));
    }

    @Test
    void deveFalharVendaSemPosicao() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(ativoRepository.findById(30L)).willReturn(Optional.of(ativo()));
        given(posicaoRepository.findByCarteiraIdAndAtivoId(10L, 30L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> new PatchPosicaoUseCase(carteiraRepository, ativoRepository, posicaoRepository)
                .vendaAtivo(1L, 30L, 2, BigDecimal.ONE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deveExcluirPosicao() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(posicaoRepository.findByCarteiraIdAndAtivoId(10L, 30L))
                .willReturn(Optional.of(Posicao.builder().id(40L).build()));

        new DeletePosicaoUseCase(carteiraRepository, posicaoRepository)
                .delete(new DeletePosicaoRequest(30L), 1L);

        verify(posicaoRepository).deleteByCarteiraIdAndAtivoId(10L, 30L);
    }

    @Test
    void deveFalharExclusaoSemPosicao() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(posicaoRepository.findByCarteiraIdAndAtivoId(10L, 30L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> new DeletePosicaoUseCase(carteiraRepository, posicaoRepository)
                .delete(new DeletePosicaoRequest(30L), 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deveCalcularKpisDaCarteira() {
        var projection = mock(PosicaoComClasseProjection.class);
        given(projection.getAtivoId()).willReturn(30L);
        given(projection.getQuantidade()).willReturn(10);
        given(projection.getPrecoMedio()).willReturn(new BigDecimal("10"));
        given(projection.getAtivoClasse()).willReturn("ACAO");
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(posicaoRepository.findByCarteiraId(10L)).willReturn(List.of(projection));
        given(cotacaoRepository.findLatestPrecoByAtivoId(30L)).willReturn(Optional.of(new BigDecimal("12")));
        given(cotacaoRepository.findFirstOfMonthPrecoByAtivoId(eq(30L), any()))
                .willReturn(Optional.of(new BigDecimal("10")));

        var result = new GetCarteiraKpisUseCase(carteiraRepository, posicaoRepository, cotacaoRepository)
                .execute(1L);

        assertThat(result.valorTotal()).isEqualByComparingTo("120.00");
        assertThat(result.valorInvestido()).isEqualByComparingTo("100.00");
        assertThat(result.lucroAcumulado()).isEqualByComparingTo("20.00");
        assertThat(result.distribuicaoPorClasse()).containsEntry("ACAO", new BigDecimal("100.00"));
    }

    @Test
    void deveRetornarScoreClassificado() {
        var score = ScoreCarteira.builder().scoreTotal(90).scoreDiversificacao(80)
                .scoreRisco(80).scoreConcentracao(80).scorePerformance(80)
                .numAtivos(3).numClasses(2).concentracaoMaxima(BigDecimal.TEN)
                .top3Concentracao(BigDecimal.TEN).indiceHerfindahl(BigDecimal.ONE).numSetores(2)
                .volatilidade(Volatilidade.Baixa).betaCarteira(BigDecimal.ONE).var95(BigDecimal.ONE)
                .vsCdi(BigDecimal.ONE).vsIbov(BigDecimal.ONE).sharpeRatio(BigDecimal.ONE).build();
        given(contaRepository.findByUsuarioId(1L)).willReturn(Optional.of(Conta.builder().id(20L).build()));
        given(carteiraRepository.findByContaId(20L)).willReturn(Optional.of(carteira()));
        given(scoreRepository.findLatestByCarteiraId(10L)).willReturn(Optional.of(score));

        var result = new GetScoreCarteiraUseCase(contaRepository, carteiraRepository, scoreRepository)
                .getScore(1L);

        assertThat(result.classificacao()).isEqualTo("Excelente");
    }

    @Test
    void deveFalharScoreSemConta() {
        given(contaRepository.findByUsuarioId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> new GetScoreCarteiraUseCase(contaRepository, carteiraRepository, scoreRepository)
                .getScore(1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void deveListarPosicoesComCotacaoAtual() {
        var projection = mock(PosicaoDetalhadaProjection.class);
        given(projection.getAtivoId()).willReturn(30L);
        given(projection.getAtivoNome()).willReturn("PETR4");
        given(projection.getAtivoCodigo()).willReturn("PETR4");
        given(projection.getAtivoTipo()).willReturn(TipoAtivo.ACAO);
        given(projection.getQuantidade()).willReturn(10);
        given(projection.getPrecoMedio()).willReturn(new BigDecimal("10"));
        given(carteiraRepository.findCarteiraIdByUsuarioId(1L)).willReturn(Optional.of(carteira()));
        given(posicaoRepository.findDetalhadaByCarteiraId(10L, null)).willReturn(List.of(projection));
        given(cotacaoRepository.findLatestByAtivoId(30L)).willReturn(
                Cotacao.builder().preco(new BigDecimal("12")).variacao(new BigDecimal("1.5")).build());

        var result = new GetPosicoesCarteiraUseCase(carteiraRepository, posicaoRepository, cotacaoRepository)
                .getDadosPosicao(1L, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValorAtual()).isEqualByComparingTo("120.00");
        assertThat(result.get(0).getVariacaoPosicao()).isEqualByComparingTo("20.00");
    }

    @Test
    void deveFalharListagemSemCarteira() {
        given(carteiraRepository.findCarteiraIdByUsuarioId(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> new GetPosicoesCarteiraUseCase(
                carteiraRepository, posicaoRepository, cotacaoRepository).getDadosPosicao(99L, null))
                .isInstanceOf(RuntimeException.class);
    }
}

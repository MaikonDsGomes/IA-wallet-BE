package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.VendaPosicaoRequest;
import com.wallet.md.domain.exception.ViolacaoRegraDeNegocio;
import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.model.Conta;
import com.wallet.md.domain.model.Posicao;
import com.wallet.md.domain.repository.carteira.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VenderPosicaoUseCaseTest {

    private final ContaRepository contaRepository = mock(ContaRepository.class);
    private final CarteiraRepository carteiraRepository = mock(CarteiraRepository.class);
    private final PosicaoRepository posicaoRepository = mock(PosicaoRepository.class);
    private final OrdemRepository ordemRepository = mock(OrdemRepository.class);
    private final MovimentacaoRepository movimentacaoRepository = mock(MovimentacaoRepository.class);
    private final VenderPosicaoUseCase useCase = new VenderPosicaoUseCase(
            contaRepository, carteiraRepository, posicaoRepository, ordemRepository, movimentacaoRepository);

    @Test
    void deveExecutarVendaCreditarSaldoERegistrarHistorico() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(
                Conta.builder().id(2L).saldo(new BigDecimal("100.00")).build()));
        when(carteiraRepository.findByContaId(2L)).thenReturn(Optional.of(Carteira.builder().id(3L).build()));
        when(posicaoRepository.findByCarteiraIdAndAtivoId(3L, 4L)).thenReturn(Optional.of(
                Posicao.builder().ativoId(4L).quantidade(10).precoMedio(new BigDecimal("8.00")).build()));
        when(ordemRepository.registrar(anyLong(), anyLong(), any(), anyInt(), any(), any(), any()))
                .thenReturn(9L);

        var response = useCase.vender(1L, new VendaPosicaoRequest(4L, 3, new BigDecimal("12.00")));

        assertThat(response.ordemId()).isEqualTo(9L);
        assertThat(response.valorBruto()).isEqualByComparingTo("36.00");
        verify(posicaoRepository).updateQuantidadeVenda(3L, 4L, 3, new BigDecimal("8.00"));
        verify(contaRepository).creditar(2L, new BigDecimal("36.00"));
        verify(movimentacaoRepository).registrar(any());
    }

    @Test
    void deveRecusarVendaAcimaDaQuantidadeDisponivel() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(Conta.builder().id(2L).build()));
        when(carteiraRepository.findByContaId(2L)).thenReturn(Optional.of(Carteira.builder().id(3L).build()));
        when(posicaoRepository.findByCarteiraIdAndAtivoId(3L, 4L)).thenReturn(Optional.of(
                Posicao.builder().ativoId(4L).quantidade(2).precoMedio(BigDecimal.TEN).build()));

        assertThatThrownBy(() -> useCase.vender(1L, new VendaPosicaoRequest(4L, 3, BigDecimal.TEN)))
                .isInstanceOf(ViolacaoRegraDeNegocio.class);
        verifyNoInteractions(ordemRepository, movimentacaoRepository);
    }
}

package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.CompraPosicaoRequest;
import com.wallet.md.domain.exception.ViolacaoRegraDeNegocio;
import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.model.Conta;
import com.wallet.md.domain.repository.carteira.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ComprarPosicaoUseCaseTest {

    private final ContaRepository contaRepository = mock(ContaRepository.class);
    private final CarteiraRepository carteiraRepository = mock(CarteiraRepository.class);
    private final PosicaoRepository posicaoRepository = mock(PosicaoRepository.class);
    private final OrdemRepository ordemRepository = mock(OrdemRepository.class);
    private final MovimentacaoRepository movimentacaoRepository = mock(MovimentacaoRepository.class);
    private final ComprarPosicaoUseCase useCase = new ComprarPosicaoUseCase(
            contaRepository, carteiraRepository, posicaoRepository, ordemRepository, movimentacaoRepository);

    @Test
    void deveDebitarSaldoCriarPosicaoOrdemEMovimentacao() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(
                Conta.builder().id(2L).saldo(new BigDecimal("100.00")).build()));
        when(carteiraRepository.findByContaId(2L)).thenReturn(Optional.of(Carteira.builder().id(3L).build()));
        when(contaRepository.debitar(2L, new BigDecimal("36.00"))).thenReturn(true);
        when(ordemRepository.registrar(anyLong(), anyLong(), any(), anyInt(), any(), any(), any()))
                .thenReturn(10L);

        var response = useCase.comprar(1L, new CompraPosicaoRequest(4L, 3, new BigDecimal("12.00")));

        assertThat(response.ordemId()).isEqualTo(10L);
        assertThat(response.valorBruto()).isEqualByComparingTo("36.00");
        verify(posicaoRepository).save(3L, 4L, 3, new BigDecimal("12.00"));
        verify(contaRepository).debitar(2L, new BigDecimal("36.00"));
        verify(movimentacaoRepository).registrar(any());
    }

    @Test
    void deveRecusarCompraSemSaldo() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(Conta.builder().id(2L).build()));
        when(carteiraRepository.findByContaId(2L)).thenReturn(Optional.of(Carteira.builder().id(3L).build()));
        when(contaRepository.debitar(2L, new BigDecimal("36.00"))).thenReturn(false);

        assertThatThrownBy(() -> useCase.comprar(1L,
                new CompraPosicaoRequest(4L, 3, new BigDecimal("12.00"))))
                .isInstanceOf(ViolacaoRegraDeNegocio.class)
                .hasMessageContaining("Saldo insuficiente");
        verifyNoInteractions(posicaoRepository, ordemRepository, movimentacaoRepository);
    }
}

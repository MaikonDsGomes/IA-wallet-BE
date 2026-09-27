package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.model.Conta;
import com.wallet.md.domain.model.Movimentacao;
import com.wallet.md.domain.model.enums.TipoMovimentacao;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.MovimentacaoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetMovimentacoesContaUseCaseTest {

    private final ContaRepository contaRepository = mock(ContaRepository.class);
    private final MovimentacaoRepository movimentacaoRepository = mock(MovimentacaoRepository.class);
    private final GetMovimentacoesContaUseCase useCase =
            new GetMovimentacoesContaUseCase(contaRepository, movimentacaoRepository);

    @Test
    void deveCalcularSaldoAposCadaMovimentacao() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(Conta.builder().id(2L).build()));
        when(movimentacaoRepository.findByContaId(2L)).thenReturn(List.of(
                movimentacao(TipoMovimentacao.DEPOSITO, "100"),
                movimentacao(TipoMovimentacao.VENDA, "25"),
                movimentacao(TipoMovimentacao.SAQUE, "10")));

        var response = useCase.listar(1L);

        assertThat(response).extracting("saldoApos")
                .containsExactly(new BigDecimal("100"), new BigDecimal("125"), new BigDecimal("115"));
    }

    private Movimentacao movimentacao(TipoMovimentacao tipo, String valor) {
        return Movimentacao.builder().tipo(tipo).valor(new BigDecimal(valor))
                .data(LocalDateTime.now()).build();
    }
}

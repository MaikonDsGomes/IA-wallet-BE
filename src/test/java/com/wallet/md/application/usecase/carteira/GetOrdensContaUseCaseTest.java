package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.model.Conta;
import com.wallet.md.domain.model.Ordem;
import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoOrdem;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.OrdemRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetOrdensContaUseCaseTest {

    private final ContaRepository contaRepository = mock(ContaRepository.class);
    private final OrdemRepository ordemRepository = mock(OrdemRepository.class);
    private final GetOrdensContaUseCase useCase = new GetOrdensContaUseCase(contaRepository, ordemRepository);

    @Test
    void deveListarOrdensDaContaAutenticada() {
        when(contaRepository.findByUsuarioId(1L)).thenReturn(Optional.of(Conta.builder().id(2L).build()));
        when(ordemRepository.findByContaId(2L)).thenReturn(List.of(Ordem.builder()
                .id(3L).ativoId(4L).tipo(TipoOrdem.VENDA).quantidade(2)
                .precoExecucao(new BigDecimal("10")).status(StatusOrdem.EXECUTADA).build()));

        var response = useCase.listar(1L);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().tipo()).isEqualTo(TipoOrdem.VENDA);
        assertThat(response.getFirst().status()).isEqualTo(StatusOrdem.EXECUTADA);
    }
}

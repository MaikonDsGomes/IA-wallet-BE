package com.wallet.md.application.usecase.ativo;

import com.wallet.md.domain.model.Ativo;
import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.repository.ativo.AtivoRepository;
import com.wallet.md.domain.repository.ativo.CotacaoAtivoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetAtivosPorTicketTest {

    @Mock
    private AtivoRepository ativoRepository;

    @Mock
    private CotacaoAtivoRepository cotacaoRepository;

    @InjectMocks
    private GetAtivosPorTicket useCase;

    private Ativo ativo() {
        return Ativo.builder().id(1L).nome("PETR4").codigo("PETR4")
                .tipo(TipoAtivo.ACAO).volatilidade(4).ativo(true).build();
    }

    @Test
    void deveListarAtivosEAnexarUltimaCotacao() {
        given(ativoRepository.findAtivos(TipoAtivo.ACAO, "PETR")).willReturn(List.of(ativo()));
        given(cotacaoRepository.findLatestByAtivoId(1L)).willReturn(Optional.of(
                Cotacao.builder().preco(new BigDecimal("32.40")).variacao(new BigDecimal("1.2")).build()));

        var resultado = useCase.listar(TipoAtivo.ACAO, "PETR");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).precoAtual()).isEqualByComparingTo("32.40");
    }

    @Test
    void deveFalharAoBuscarAtivoInexistente() {
        given(ativoRepository.findByCodigo("INVALIDO")).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorCodigo("INVALIDO"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ativo");
    }
}

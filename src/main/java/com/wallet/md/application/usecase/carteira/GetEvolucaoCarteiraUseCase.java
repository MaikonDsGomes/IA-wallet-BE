package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.EvolucaoCarteiraResponse;
import com.wallet.md.domain.model.enums.TipoMovimentacao;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class GetEvolucaoCarteiraUseCase {
    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public List<EvolucaoCarteiraResponse> listar(Long usuarioId) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new com.wallet.md.domain.exception.ResourceNotFoundException("Conta", usuarioId));
        BigDecimal saldo = BigDecimal.ZERO;
        var result = new java.util.ArrayList<EvolucaoCarteiraResponse>();
        for (var m : movimentacaoRepository.findByContaId(conta.getId())) {
            BigDecimal entrada = isEntrada(m.getTipo()) ? m.getValor() : BigDecimal.ZERO;
            BigDecimal saida = isEntrada(m.getTipo()) ? BigDecimal.ZERO : m.getValor();
            saldo = saldo.add(entrada).subtract(saida);
            result.add(new EvolucaoCarteiraResponse(m.getData(), saldo, entrada, saida, entrada.subtract(saida)));
        }
        return result;
    }
    private boolean isEntrada(TipoMovimentacao tipo) {
        return tipo == TipoMovimentacao.DEPOSITO || tipo == TipoMovimentacao.VENDA;
    }
}

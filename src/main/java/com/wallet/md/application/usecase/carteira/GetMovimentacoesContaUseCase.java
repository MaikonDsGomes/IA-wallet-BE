package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.MovimentacaoResponse;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class GetMovimentacoesContaUseCase {

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public List<MovimentacaoResponse> listar(Long usuarioId) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        var saldo = BigDecimal.ZERO;
        var respostas = new java.util.ArrayList<MovimentacaoResponse>();
        for (var movimentacao : movimentacaoRepository.findByContaId(conta.getId())) {
            saldo = switch (movimentacao.getTipo()) {
                case DEPOSITO, VENDA -> saldo.add(movimentacao.getValor());
                case SAQUE -> saldo.subtract(movimentacao.getValor());
                case TRANSFERENCIA -> saldo;
            };
            respostas.add(new MovimentacaoResponse(
                    movimentacao.getId(), movimentacao.getTipo(), movimentacao.getValor(),
                    movimentacao.getData(), saldo));
        }
        return respostas;
    }
}

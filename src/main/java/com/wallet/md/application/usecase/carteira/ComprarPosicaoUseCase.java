package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.CompraPosicaoRequest;
import com.wallet.md.application.dto.response.carteira.CompraPosicaoResponse;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.exception.ViolacaoRegraDeNegocio;
import com.wallet.md.domain.model.Movimentacao;
import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoMovimentacao;
import com.wallet.md.domain.model.enums.TipoOrdem;
import com.wallet.md.domain.repository.carteira.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ComprarPosicaoUseCase {

    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final PosicaoRepository posicaoRepository;
    private final OrdemRepository ordemRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public CompraPosicaoResponse comprar(Long usuarioId, CompraPosicaoRequest request) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        var carteira = carteiraRepository.findByContaId(conta.getId())
                .orElseThrow(() -> new NotFoundException("Carteira não encontrada"));
        var valorBruto = request.precoUnitario().multiply(BigDecimal.valueOf(request.quantidade()));

        if (!contaRepository.debitar(conta.getId(), valorBruto)) {
            throw new ViolacaoRegraDeNegocio("Saldo insuficiente para realizar a compra");
        }

        var executadaEm = LocalDateTime.now();
        posicaoRepository.save(carteira.getId(), request.ativoId(), request.quantidade(), request.precoUnitario());
        var ordemId = ordemRepository.registrar(
                conta.getId(), request.ativoId(), TipoOrdem.COMPRA, request.quantidade(),
                request.precoUnitario(), StatusOrdem.EXECUTADA, executadaEm);
        movimentacaoRepository.registrar(Movimentacao.builder()
                .contaId(conta.getId())
                .tipo(TipoMovimentacao.COMPRA)
                .valor(valorBruto)
                .data(executadaEm)
                .build());

        return new CompraPosicaoResponse(
                ordemId, request.ativoId(), request.quantidade(), request.precoUnitario(),
                valorBruto, conta.getSaldo().subtract(valorBruto), executadaEm);
    }
}

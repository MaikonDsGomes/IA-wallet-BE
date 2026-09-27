package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.VendaPosicaoRequest;
import com.wallet.md.application.dto.response.carteira.VendaPosicaoResponse;
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
public class VenderPosicaoUseCase {

    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final PosicaoRepository posicaoRepository;
    private final OrdemRepository ordemRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public VendaPosicaoResponse vender(Long usuarioId, VendaPosicaoRequest request) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        var carteira = carteiraRepository.findByContaId(conta.getId())
                .orElseThrow(() -> new NotFoundException("Carteira não encontrada"));
        var posicao = posicaoRepository.findByCarteiraIdAndAtivoId(carteira.getId(), request.ativoId())
                .orElseThrow(() -> new NotFoundException("Posição não encontrada para o ativo informado"));

        if (request.quantidade() > posicao.getQuantidade()) {
            throw new ViolacaoRegraDeNegocio("Quantidade de venda superior à posição disponível");
        }

        var valorBruto = request.precoUnitario()
                .multiply(BigDecimal.valueOf(request.quantidade()));
        var executadaEm = LocalDateTime.now();

        posicaoRepository.updateQuantidadeVenda(
                carteira.getId(), request.ativoId(), request.quantidade(), posicao.getPrecoMedio());
        var ordemId = ordemRepository.registrar(
                conta.getId(), request.ativoId(), TipoOrdem.VENDA, request.quantidade(),
                request.precoUnitario(), StatusOrdem.EXECUTADA, executadaEm);
        contaRepository.creditar(conta.getId(), valorBruto);
        movimentacaoRepository.registrar(Movimentacao.builder()
                .contaId(conta.getId())
                .tipo(TipoMovimentacao.VENDA)
                .valor(valorBruto)
                .data(executadaEm)
                .build());

        return new VendaPosicaoResponse(
                ordemId, request.ativoId(), request.quantidade(), request.precoUnitario(),
                valorBruto, conta.getSaldo().add(valorBruto), executadaEm);
    }
}

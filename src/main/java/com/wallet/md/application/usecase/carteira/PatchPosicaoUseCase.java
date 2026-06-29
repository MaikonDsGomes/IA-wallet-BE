package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.repository.carteira.AtivoRepository;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class PatchPosicaoUseCase {
    private final CarteiraRepository carteiraRepository;
    private final AtivoRepository ativoRepository;
    private final PosicaoRepository posicaoRepository;

    public void vendaAtivo(Long usuarioId, Long ativoId, Integer quantidade, BigDecimal precoUnitario) {
        var carteira = carteiraRepository.findCarteiraIdByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Carteira não encontrada"));

        var ativo = ativoRepository.findById(ativoId)
                .orElseThrow(() -> new NotFoundException("Ativo não encontrado"));

        posicaoRepository.findByCarteiraIdAndAtivoId(carteira.getId(), ativo.getId())
                .orElseThrow(() -> new NotFoundException("Posição não encontrada para o ativo na carteira"));

        posicaoRepository.updateQuantidadeVenda(carteira.getId(), ativo.getId(), quantidade, precoUnitario);
    }

}

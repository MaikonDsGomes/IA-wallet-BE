package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.AdicionarPosicaoRequest;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.repository.carteira.AtivoRepository;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostPosicaoUseCase {

    private final CarteiraRepository carteiraRepository;
    private final AtivoRepository ativoRepository;
    private final PosicaoRepository posicaoRepository;

    public void post(Long usuarioId, AdicionarPosicaoRequest request) {

        var carteira = carteiraRepository.findCarteiraIdByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Carteira não encontrada"));

        var ativo = ativoRepository.findByCodigo(request.codigo())
                .orElseThrow(() -> new NotFoundException(
                        "Ativo não encontrado com código: " + request.codigo()));

        if (!ativo.getTipo().equals(request.tipo())) {
            throw new IllegalArgumentException(
                    "Tipo informado não corresponde ao ativo. Esperado: " + ativo.getTipo());
        }

        posicaoRepository.save(
                carteira.getId(),
                ativo.getId(),
                request.quantidade(),
                request.precoMedio()
        );
    }
}
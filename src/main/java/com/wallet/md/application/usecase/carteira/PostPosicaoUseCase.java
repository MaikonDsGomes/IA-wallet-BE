package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.PostPosicaoRequest;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.repository.carteira.AtivoRepository;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class PostPosicaoUseCase {

    private final CarteiraRepository carteiraRepository;
    private final AtivoRepository ativoRepository;
    private final PosicaoRepository posicaoRepository;
    private final AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase;

    public PostPosicaoUseCase(CarteiraRepository carteiraRepository, AtivoRepository ativoRepository,
                              PosicaoRepository posicaoRepository) {
        this(carteiraRepository, ativoRepository, posicaoRepository, null);
    }

    @Transactional
    public void post(Long usuarioId, PostPosicaoRequest request) {

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
        if (agendarRecalculoScoreUseCase != null) {
            agendarRecalculoScoreUseCase.agendar(carteira.getId());
        }
    }
}
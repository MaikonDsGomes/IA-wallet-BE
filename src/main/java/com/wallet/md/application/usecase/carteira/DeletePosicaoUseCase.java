package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.request.carteira.DeletePosicaoRequest;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.exception.ViolacaoRegraDeNegocio;
import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.CotacaoRepository;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DeletePosicaoUseCase {
    private final CarteiraRepository carteiraRepository;
    private final PosicaoRepository posicaoRepository;
    private final AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase;

    public DeletePosicaoUseCase(CarteiraRepository carteiraRepository, PosicaoRepository posicaoRepository) {
        this(carteiraRepository, posicaoRepository, null);
    }

    @Transactional
    public void delete(DeletePosicaoRequest request, Long usuarioId) {

        Carteira carteira = carteiraRepository.findCarteiraIdByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Carteira do usuário não encontrada"));


        posicaoRepository.findByCarteiraIdAndAtivoId(carteira.getId(), request.ativoId())
                .orElseThrow(() -> new NotFoundException("Posição não encontrada para o ativo informado"));

        posicaoRepository.deleteByCarteiraIdAndAtivoId(carteira.getId(), request.ativoId());
        if (agendarRecalculoScoreUseCase != null) {
            agendarRecalculoScoreUseCase.agendar(carteira.getId());
        }
    }

}

package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.RecomendacaoResponse;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.RecomendacaoRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetRecomendacoesCarteiraUseCase {

    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final RecomendacaoRepository recomendacaoRepository;

    public List<RecomendacaoResponse> getRecomendacoes(Long usuarioId) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta", usuarioId));
        var carteira = carteiraRepository.findByContaId(conta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira", conta.getId()));

        return recomendacaoRepository.findAtivasByCarteiraId(carteira.getId()).stream()
                .map(recomendacao -> new RecomendacaoResponse(
                        recomendacao.getId(),
                        recomendacao.getAtivoId(),
                        recomendacao.getTitulo(),
                        recomendacao.getDescricao(),
                        recomendacao.getCategoria(),
                        recomendacao.getPrioridade(),
                        recomendacao.getImpactoPts(),
                        recomendacao.getGeradaEm()
                ))
                .toList();
    }
}

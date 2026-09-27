package com.wallet.md.application.usecase.carteira;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class SolicitarAtualizacaoRecomendacoesUseCase {

    private final GerarRecomendacoesCarteiraUseCase gerarRecomendacoesCarteiraUseCase;

    @Async
    public void solicitar(Long usuarioId) {
        gerarRecomendacoesCarteiraUseCase.gerar(usuarioId);
    }
}

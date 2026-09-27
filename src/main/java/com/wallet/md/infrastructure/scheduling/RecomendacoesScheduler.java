package com.wallet.md.infrastructure.scheduling;

import com.wallet.md.application.usecase.carteira.SolicitarAtualizacaoRecomendacoesUseCase;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecomendacoesScheduler {

    private final CarteiraRepository carteiraRepository;
    private final SolicitarAtualizacaoRecomendacoesUseCase solicitarAtualizacaoRecomendacoesUseCase;

    @Scheduled(cron = "${investwallet.recomendacoes.cron:0 0 3 ? * SUN}")
    public void atualizarSemanalmente() {
        carteiraRepository.findUsuarioIds()
                .forEach(solicitarAtualizacaoRecomendacoesUseCase::solicitar);
        log.info("Atualização semanal de recomendações enfileirada");
    }
}

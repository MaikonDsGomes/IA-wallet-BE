package com.wallet.md.infrastructure.scheduling;

import com.wallet.md.application.usecase.carteira.ProcessarRecalculoScoreUseCase;
import com.wallet.md.domain.repository.carteira.RecalculoScoreRepository;
import com.wallet.md.infrastructure.config.ScoreMetodologiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreRecalculoScheduler {

    private final RecalculoScoreRepository recalculoRepository;
    private final ProcessarRecalculoScoreUseCase processarRecalculoScoreUseCase;
    private final ScoreMetodologiaProperties properties;

    @Scheduled(fixedDelayString = "${investwallet.score.intervalo-scheduler-segundos:60}000")
    public void processarVencidos() {
        var agora = LocalDateTime.now();
        var limite = agora.minusMinutes(properties.getStaleProcessingMinutos());
        recalculoRepository.findIdsVencidos(agora, limite)
                .forEach(processarRecalculoScoreUseCase::processar);
    }
}

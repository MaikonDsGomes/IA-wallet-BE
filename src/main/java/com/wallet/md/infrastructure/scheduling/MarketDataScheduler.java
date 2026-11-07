package com.wallet.md.infrastructure.scheduling;

import com.wallet.md.application.usecase.mercado.IngerirDadosMercadoUseCase;
import com.wallet.md.infrastructure.config.MarketDataProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataScheduler {

    private final IngerirDadosMercadoUseCase useCase;
    private final MarketDataProperties properties;

    @Scheduled(cron = "${investwallet.market.cron:0 30 19 * * MON-FRI}")
    public void ingerirDiariamente() {
        if (properties.isEnabled()) {
            useCase.executar();
        }
    }
}

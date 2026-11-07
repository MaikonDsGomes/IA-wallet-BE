package com.wallet.md.application.usecase.mercado;

import com.wallet.md.domain.model.MarketPoint;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import com.wallet.md.domain.repository.mercado.MarketDataProvider;
import com.wallet.md.domain.repository.mercado.MarketSeriesRepository;
import com.wallet.md.infrastructure.config.MarketDataProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class IngerirDadosMercadoUseCase {

    private final PosicaoRepository posicaoRepository;
    private final MarketDataProvider provider;
    private final MarketSeriesRepository seriesRepository;
    private final MarketDataProperties properties;

    @Async
    public void executar() {
        if (!properties.isEnabled()) {
            log.debug("Ingestão de mercado desabilitada");
            return;
        }

        var end = LocalDate.now();
        var start = end.minusDays(properties.getHistoricoDias());
        var symbols = new LinkedHashSet<>(posicaoRepository.findAllForMarketData().stream()
                .map(p -> p.getAtivoCodigo())
                .filter(code -> code != null && !code.isBlank())
                .toList());
        try {
            symbols.forEach(symbol -> persistir(provider.fetchAssetHistory(symbol, start, end)));
            persistir(provider.fetchBenchmarkHistory(properties.getBenchmarkSymbol(), start, end));
            persistir(provider.fetchMacroHistory(properties.getCdiSymbol(), start, end));
            log.info("Ingestão de mercado concluída: {} ativos, janela {} a {}", symbols.size(), start, end);
        } catch (RuntimeException exception) {
            log.error("Falha na ingestão de dados de mercado", exception);
        }
    }

    private void persistir(List<MarketPoint> points) {
        if (points != null && !points.isEmpty()) {
            seriesRepository.upsertAll(points);
        }
    }
}

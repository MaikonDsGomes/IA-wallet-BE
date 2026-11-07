package com.wallet.md.domain.repository.mercado;

import com.wallet.md.domain.model.MarketPoint;

import java.time.LocalDate;
import java.util.List;

public interface MarketDataProvider {
    List<MarketPoint> fetchAssetHistory(String symbol, LocalDate start, LocalDate end);

    List<MarketPoint> fetchBenchmarkHistory(String instrument, LocalDate start, LocalDate end);

    List<MarketPoint> fetchMacroHistory(String series, LocalDate start, LocalDate end);
}

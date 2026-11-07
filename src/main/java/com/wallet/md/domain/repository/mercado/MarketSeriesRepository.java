package com.wallet.md.domain.repository.mercado;

import com.wallet.md.domain.model.MarketPoint;

import java.time.LocalDate;
import java.util.List;

public interface MarketSeriesRepository {
    void upsertAll(List<MarketPoint> points);

    List<MarketPoint> findByInstrumentoAndTipo(String instrumento, String tipo, LocalDate start, LocalDate end);
}

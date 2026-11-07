package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.MarketPoint;
import com.wallet.md.domain.repository.mercado.MarketSeriesRepository;
import com.wallet.md.infrastructure.persistence.repository.JpaMarketSeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarketSeriesRepositoryAdapter implements MarketSeriesRepository {

    private final JpaMarketSeriesRepository repository;

    @Override
    @Transactional
    public void upsertAll(List<MarketPoint> points) {
        points.forEach(point -> repository.upsert(
                point.getInstrumento(), point.getTipo(), point.getData(),
                point.getValor(), point.getValorAjustado(), point.getFonte()));
    }

    @Override
    public List<MarketPoint> findByInstrumentoAndTipo(
            String instrumento, String tipo, LocalDate start, LocalDate end) {
        return repository.findByInstrumentoAndTipoAndDataReferenciaBetweenOrderByDataReferenciaAsc(
                        instrumento, tipo, start, end).stream()
                .map(entity -> MarketPoint.builder()
                        .instrumento(entity.getInstrumento())
                        .tipo(entity.getTipo())
                        .data(entity.getDataReferencia())
                        .valor(entity.getValor())
                        .valorAjustado(entity.getValorAjustado())
                        .fonte(entity.getFonte())
                        .build())
                .toList();
    }
}

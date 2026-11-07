package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.MarketSeriesEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JpaMarketSeriesRepository extends JpaRepository<MarketSeriesEntity, Long> {
    List<MarketSeriesEntity> findByInstrumentoAndTipoAndDataReferenciaBetweenOrderByDataReferenciaAsc(
            String instrumento, String tipo, LocalDate start, LocalDate end);

    @Modifying
    @Query(value = """
            INSERT INTO market_series
                (instrumento, tipo, data_referencia, valor, valor_ajustado, fonte, coletado_em)
            VALUES (:instrumento, :tipo, :data, :valor, :valorAjustado, :fonte, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                valor = VALUES(valor),
                valor_ajustado = VALUES(valor_ajustado),
                fonte = VALUES(fonte),
                coletado_em = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    void upsert(@Param("instrumento") String instrumento,
                @Param("tipo") String tipo,
                @Param("data") LocalDate data,
                @Param("valor") java.math.BigDecimal valor,
                @Param("valorAjustado") java.math.BigDecimal valorAjustado,
                @Param("fonte") String fonte);
}

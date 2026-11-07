package com.wallet.md.infrastructure.external.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.md.domain.model.MarketPoint;
import com.wallet.md.domain.repository.mercado.MarketDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class BrapiMarketDataProvider implements MarketDataProvider {

    private static final String SOURCE = "BRAPI";
    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String assetRange;
    private final String benchmarkSymbol;
    private final String cdiSymbol;
    private final String apiKey;
    private final int maxAttempts;

    public BrapiMarketDataProvider(
            RestClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${investwallet.market.provider.base-url:https://brapi.dev/api}") String baseUrl,
            @Value("${investwallet.market.provider.asset-range:1y}") String assetRange,
            @Value("${investwallet.market.provider.benchmark-symbol:^BVSP}") String benchmarkSymbol,
            @Value("${investwallet.market.provider.cdi-symbol:cdi}") String cdiSymbol,
            @Value("${investwallet.market.provider.api-key:}") String apiKey,
            @Value("${investwallet.market.provider.max-attempts:3}") int maxAttempts) {
        this.client = builder.baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
        this.assetRange = assetRange;
        this.benchmarkSymbol = benchmarkSymbol;
        this.cdiSymbol = cdiSymbol;
        this.apiKey = apiKey;
        this.maxAttempts = Math.max(1, maxAttempts);
    }

    @Override
    public List<MarketPoint> fetchAssetHistory(String symbol, LocalDate start, LocalDate end) {
        return fetchQuoteHistory(symbol, start, end, "ATIVO");
    }

    @Override
    public List<MarketPoint> fetchBenchmarkHistory(String instrument, LocalDate start, LocalDate end) {
        return fetchQuoteHistory(instrument == null ? benchmarkSymbol : instrument, start, end, "IBOVESPA");
    }

    @Override
    public List<MarketPoint> fetchMacroHistory(String series, LocalDate start, LocalDate end) {
        var response = getMacro(series, start, end);
        var points = new ArrayList<MarketPoint>();
        var seriesNode = response == null ? null : response.path("results").path(0);
        var observations = seriesNode == null ? null : seriesNode.path("series");
        if (observations != null && observations.isArray()) {
            for (var item : observations) {
                if (!item.hasNonNull("date") || !item.hasNonNull("value")) continue;
                var date = LocalDate.parse(item.path("date").asText());
                var value = item.path("value").decimalValue();
                points.add(MarketPoint.builder()
                        .instrumento(series == null ? cdiSymbol : series)
                        .tipo("CDI")
                        .data(date)
                        .valor(value)
                        .valorAjustado(value)
                        .fonte(SOURCE)
                        .build());
            }
        }
        return points;
    }

    private List<MarketPoint> fetchQuoteHistory(
            String symbol, LocalDate start, LocalDate end, String type) {
        var response = getQuote(symbol, start, end);
        var points = new ArrayList<MarketPoint>();
        var history = response == null ? null : response.path("results").path(0).path("historicalDataPrice");
        if (history == null || !history.isArray()) return points;
        for (var item : history) {
            var date = Instant.ofEpochSecond(item.path("date").asLong()).atZone(ZoneOffset.UTC).toLocalDate();
            points.add(MarketPoint.builder()
                    .instrumento(symbol)
                    .tipo(type)
                    .data(date)
                    .valor(decimal(item, "close"))
                    .valorAjustado(decimal(item, "adjustedClose"))
                    .fonte(SOURCE)
                    .build());
        }
        return points;
    }

    private BigDecimal decimal(JsonNode node, String field) {
        return node.path(field).isNumber() ? node.path(field).decimalValue() : null;
    }

    private void addAuthorization(org.springframework.http.HttpHeaders headers) {
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("Authorization", apiKey);
        }
    }

    private JsonNode getQuote(String symbol, LocalDate start, LocalDate end) {
            return retry(() -> client.get()
                    .uri(uri -> uri.path("/quote/{symbol}")
                            .queryParam("range", assetRange)
                            .queryParam("interval", "1d")
                            .queryParam("startDate", start)
                            .queryParam("endDate", end)
                            .build(symbol))
                    .headers(headers -> addAuthorization(headers))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class));
        }

    private JsonNode getMacro(String series, LocalDate start, LocalDate end) {
            return retry(() -> client.get()
                    .uri(uri -> uri.path("/v2/macro")
                            .queryParam("symbols", series == null ? cdiSymbol : series)
                            .queryParam("startDate", start)
                            .queryParam("endDate", end)
                            .queryParam("limit", 5000)
                            .build())
                    .headers(headers -> addAuthorization(headers))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class));
        }

    private JsonNode retry(java.util.function.Supplier<JsonNode> request) {
            RestClientException last = null;
            for (var attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    return request.get();
                } catch (RestClientException exception) {
                    last = exception;
                    if (attempt < maxAttempts) {
                        try {
                            Thread.sleep(250L * attempt);
                        } catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            break;
                    }
                }
            }
        }
        throw last == null ? new IllegalStateException("Falha na consulta de mercado") : last;
    }
}

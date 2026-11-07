package com.wallet.md.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "investwallet.market")
public class MarketDataProperties {
    private boolean enabled;
    private int historicoDias = 365;
    private int timeoutSegundos = 10;
    private int maxTentativas = 3;
    private String benchmarkSymbol = "^BVSP";
    private String cdiSymbol = "cdi";
    private Provider provider = new Provider();

    @Getter
    @Setter
    public static class Provider {
        private String name = "brapi";
        private String baseUrl = "https://brapi.dev/api";
        private String apiKey = "";
        private String assetRange = "1y";
        private String benchmarkSymbol = "^BVSP";
        private String cdiSymbol = "cdi";
    }
}

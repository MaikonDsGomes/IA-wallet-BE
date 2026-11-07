package com.wallet.md.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "notification.email")
public class EmailProperties {

    private boolean enabled;
    private String provider = "default";
    private String apiKey = "";
    private String from = "";
    private String baseUrl = "https://api.resend.com";
    private int timeoutSeconds = 10;
    private int maxAttempts = 2;
}

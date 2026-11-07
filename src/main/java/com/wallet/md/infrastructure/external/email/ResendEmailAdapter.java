package com.wallet.md.infrastructure.external.email;

import com.wallet.md.domain.exception.EmailIntegrationException;
import com.wallet.md.domain.model.EmailMessage;
import com.wallet.md.domain.model.enums.EmailBodyFormat;
import com.wallet.md.domain.repository.notification.EmailSenderPort;
import com.wallet.md.infrastructure.config.EmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class ResendEmailAdapter implements EmailSenderPort {

    private static final String PROVIDER = "resend";
    private final RestClient client;
    private final EmailProperties properties;

    public ResendEmailAdapter(RestClient.Builder builder, EmailProperties properties) {
        this.properties = properties;
        var timeout = Duration.ofSeconds(Math.max(1, properties.getTimeoutSeconds()));
        var requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(timeout).build());
        requestFactory.setReadTimeout(timeout);
        this.client = builder
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public void sendEmail(EmailMessage message) {
        if (!properties.isEnabled()) {
            log.debug("Envio de e-mail desabilitado; provider={}", PROVIDER);
            return;
        }
        validateConfiguration();
        var maxAttempts = Math.max(1, properties.getMaxAttempts());

        var payload = new java.util.HashMap<String, Object>();
        payload.put("from", properties.getFrom());
        payload.put("to", List.of(message.destination()));
        payload.put("subject", message.subject());
        payload.put(message.format() == EmailBodyFormat.HTML ? "html" : "text", message.body());

        RestClientException last = null;
        for (var attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                client.post()
                        .uri("/emails")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .toBodilessEntity();
                log.info("E-mail enviado pelo provider={}", PROVIDER);
                return;
            } catch (RestClientResponseException exception) {
                last = exception;
                if (!shouldRetry(exception) || attempt == maxAttempts) {
                    throw translate(exception);
                }
                waitBeforeRetry(attempt);
            } catch (RestClientException exception) {
                last = exception;
                if (attempt == maxAttempts) {
                    throw new EmailIntegrationException(
                            "Falha de comunicação com o provedor de e-mail", exception);
                }
                waitBeforeRetry(attempt);
            }
        }
        throw new EmailIntegrationException("Falha de comunicação com o provedor de e-mail", last);
    }

    private void validateConfiguration() {
        if (!"resend".equalsIgnoreCase(properties.getProvider())
                && !"default".equalsIgnoreCase(properties.getProvider())) {
            throw new EmailIntegrationException("Provider de e-mail não suportado: " + properties.getProvider());
        }
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new EmailIntegrationException("Provider de e-mail habilitado sem API key configurada");
        }
        if (properties.getFrom() == null || properties.getFrom().isBlank()) {
            throw new EmailIntegrationException("Provider de e-mail habilitado sem remetente configurado");
        }
    }

    private boolean shouldRetry(RestClientResponseException exception) {
        return exception.getStatusCode().value() == 429 || exception.getStatusCode().is5xxServerError();
    }

    private EmailIntegrationException translate(RestClientResponseException exception) {
        var status = exception.getStatusCode().value();
        var message = switch (status) {
            case 401, 403 -> "Credencial ou remetente rejeitado pelo provedor de e-mail";
            case 400, 422 -> "Mensagem rejeitada pelo provedor de e-mail";
            case 429 -> "Limite do provedor de e-mail excedido";
            default -> "Provedor de e-mail indisponível";
        };
        log.warn("Falha no provider={} status={}", PROVIDER, status);
        return new EmailIntegrationException(message, exception);
    }

    private void waitBeforeRetry(int attempt) {
        try {
            Thread.sleep(250L * attempt);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new EmailIntegrationException("Envio de e-mail interrompido", exception);
        }
    }
}

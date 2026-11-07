package com.wallet.md.domain.exception;

public class EmailIntegrationException extends RuntimeException {

    public EmailIntegrationException(String message) {
        super(message);
    }

    public EmailIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

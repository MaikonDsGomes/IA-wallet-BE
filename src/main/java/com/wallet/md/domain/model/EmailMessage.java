package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.EmailBodyFormat;
import lombok.Builder;
@Builder
public record EmailMessage(
        String destination,
        String subject,
        String body,
        EmailBodyFormat format) {

    public EmailMessage {
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destinatário do e-mail é obrigatório");
        }
        if (!destination.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Destinatário do e-mail inválido");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Assunto do e-mail é obrigatório");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Corpo do e-mail é obrigatório");
        }
        if (format == null) {
            throw new IllegalArgumentException("Formato do e-mail é obrigatório");
        }
    }

    public EmailMessage(String destination, String subject, String body) {
        this(destination, subject, body, EmailBodyFormat.PLAIN_TEXT);
    }
}

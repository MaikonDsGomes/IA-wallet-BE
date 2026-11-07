package com.wallet.md.application.usecase.notification;

import com.wallet.md.domain.model.EmailMessage;
import com.wallet.md.domain.repository.notification.EmailSenderPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendEmailUseCase {

    private final EmailSenderPort emailSenderPort;

    public void sendEmail(String destination, String subject, String body) {
        sendEmail(new EmailMessage(destination, subject, body));
    }

    public void sendEmail(String destination, String subject, String body,
                          com.wallet.md.domain.model.enums.EmailBodyFormat format) {
        sendEmail(EmailMessage.builder()
                .destination(destination)
                .subject(subject)
                .body(body)
                .format(format)
                .build());
    }

    public void sendEmail(EmailMessage message) {
        emailSenderPort.sendEmail(message);
    }

    public void execute(String destination, String subject, String body) {
        sendEmail(destination, subject, body);
    }

    public void execute(String destination, String subject, String body,
                        com.wallet.md.domain.model.enums.EmailBodyFormat format) {
        sendEmail(EmailMessage.builder()
                .destination(destination)
                .subject(subject)
                .body(body)
                .format(format)
                .build());
    }

    public void execute(EmailMessage message) {
        sendEmail(message);
    }
}

package com.wallet.md.domain.repository.notification;

import com.wallet.md.domain.model.EmailMessage;

public interface EmailSenderPort {
    void sendEmail(EmailMessage message);
}

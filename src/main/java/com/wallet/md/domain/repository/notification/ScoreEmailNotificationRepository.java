package com.wallet.md.domain.repository.notification;

import com.wallet.md.domain.model.ScoreEmailNotification;

import java.util.Optional;

public interface ScoreEmailNotificationRepository {
    Optional<ScoreEmailNotification> findByRecalculoIdForUpdate(Long recalculoId);

    ScoreEmailNotification save(ScoreEmailNotification notification);
}

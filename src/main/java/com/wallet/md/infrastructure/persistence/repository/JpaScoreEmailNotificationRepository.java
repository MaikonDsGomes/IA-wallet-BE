package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.ScoreEmailNotificationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaScoreEmailNotificationRepository
        extends JpaRepository<ScoreEmailNotificationEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from ScoreEmailNotificationEntity n where n.recalculoId = :recalculoId")
    Optional<ScoreEmailNotificationEntity> findByRecalculoIdForUpdate(
            @Param("recalculoId") Long recalculoId);
}

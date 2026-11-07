package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.ScoreEmailNotification;
import com.wallet.md.domain.repository.notification.ScoreEmailNotificationRepository;
import com.wallet.md.infrastructure.persistence.entity.ScoreEmailNotificationEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaScoreEmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScoreEmailNotificationRepositoryAdapter implements ScoreEmailNotificationRepository {

    private final JpaScoreEmailNotificationRepository repository;

    @Override
    public Optional<ScoreEmailNotification> findByRecalculoIdForUpdate(Long recalculoId) {
        return repository.findByRecalculoIdForUpdate(recalculoId).map(this::toDomain);
    }

    @Override
    public ScoreEmailNotification save(ScoreEmailNotification notification) {
        var entity = ScoreEmailNotificationEntity.builder()
                .id(notification.getId())
                .recalculoId(notification.getRecalculoId())
                .status(notification.getStatus())
                .tentativas(notification.getTentativas())
                .ultimoErro(notification.getUltimoErro())
                .enviadoEm(notification.getEnviadoEm())
                .criadoEm(notification.getCriadoEm())
                .atualizadoEm(notification.getAtualizadoEm())
                .build();
        return toDomain(repository.save(entity));
    }

    private ScoreEmailNotification toDomain(ScoreEmailNotificationEntity entity) {
        return ScoreEmailNotification.builder()
                .id(entity.getId())
                .recalculoId(entity.getRecalculoId())
                .status(entity.getStatus())
                .tentativas(entity.getTentativas())
                .ultimoErro(entity.getUltimoErro())
                .enviadoEm(entity.getEnviadoEm())
                .criadoEm(entity.getCriadoEm())
                .atualizadoEm(entity.getAtualizadoEm())
                .build();
    }
}

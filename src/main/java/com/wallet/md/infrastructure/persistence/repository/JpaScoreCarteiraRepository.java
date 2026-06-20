package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.ScoreCarteiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaScoreCarteiraRepository extends JpaRepository<ScoreCarteiraEntity, Long> {
    // Busca o score mais recente da carteira
    Optional<ScoreCarteiraEntity> findTopByCarteiraIdOrderByCalculadoEmDesc(Long carteiraId);
}

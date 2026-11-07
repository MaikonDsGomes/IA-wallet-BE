package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.RecalculoScore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecalculoScoreRepository {
    Optional<RecalculoScore> findByCarteiraId(Long carteiraId);

    Optional<RecalculoScore> findByIdForUpdate(Long id);

    List<Long> findIdsVencidos(LocalDateTime agora, LocalDateTime limiteProcessamento);

    RecalculoScore save(RecalculoScore recalculo);
}

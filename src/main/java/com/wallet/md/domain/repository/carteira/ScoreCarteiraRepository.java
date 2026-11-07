package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.StatusScore;
import java.util.Optional;

public interface ScoreCarteiraRepository {
    Optional<ScoreCarteira> findLatestByCarteiraId(Long carteiraId);

    ScoreCarteira save(ScoreCarteira score);
}
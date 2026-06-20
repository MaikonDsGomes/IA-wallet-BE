package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.ScoreCarteira;
import java.util.Optional;

public interface ScoreCarteiraRepository {
    Optional<ScoreCarteira> findLatestByCarteiraId(Long carteiraId);
}
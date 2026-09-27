package com.wallet.md.application.service;

import com.wallet.md.domain.model.Recomendacao;
import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.PerfilRisco;

public interface RecommendationTextProvider {

    Recomendacao improve(Recomendacao recommendation, ScoreCarteira score, PerfilRisco profile);
}

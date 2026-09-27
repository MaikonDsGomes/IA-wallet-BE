package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Recomendacao;

import java.util.List;

public interface RecomendacaoRepository {
    List<Recomendacao> findAtivasByCarteiraId(Long carteiraId);

    void substituirAtivas(Long carteiraId, List<Recomendacao> recomendacoes);
}

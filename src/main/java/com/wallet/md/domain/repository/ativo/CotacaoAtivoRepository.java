package com.wallet.md.domain.repository.ativo;

import com.wallet.md.domain.model.Cotacao;

import java.util.List;
import java.util.Optional;

public interface CotacaoAtivoRepository {
    Optional<Cotacao> findLatestByAtivoId(Long ativoId);

    List<Cotacao> findHistoricoByAtivoId(Long ativoId);
}

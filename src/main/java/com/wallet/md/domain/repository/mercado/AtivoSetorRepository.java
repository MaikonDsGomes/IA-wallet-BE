package com.wallet.md.domain.repository.mercado;

import com.wallet.md.domain.model.AtivoSetor;

import java.util.List;
import java.util.Optional;

public interface AtivoSetorRepository {
    Optional<AtivoSetor> findByAtivoId(Long ativoId);

    List<AtivoSetor> findByAtivoIds(List<Long> ativoIds);
}

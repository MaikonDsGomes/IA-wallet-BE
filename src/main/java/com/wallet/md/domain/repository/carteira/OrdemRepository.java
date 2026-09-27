package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoOrdem;
import com.wallet.md.domain.model.Ordem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrdemRepository {
    Long registrar(Long contaId, Long ativoId, TipoOrdem tipo, Integer quantidade,
                   BigDecimal precoExecucao, StatusOrdem status, LocalDateTime criadaEm);

    List<Ordem> findByContaId(Long contaId);
}

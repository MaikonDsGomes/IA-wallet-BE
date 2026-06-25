package com.wallet.md.domain.repository.carteira;

import aj.org.objectweb.asm.commons.Remapper;
import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CotacaoRepository {

    Cotacao findLatestByAtivoId(Long ativoId);

    Optional<BigDecimal> findFirstOfMonthPrecoByAtivoId(Long ativoId, LocalDate mesAno);

    Optional<BigDecimal> findLatestPrecoByAtivoId(Long ativoId);
}

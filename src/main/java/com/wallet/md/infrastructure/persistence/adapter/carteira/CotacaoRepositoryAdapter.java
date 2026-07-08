package com.wallet.md.infrastructure.persistence.adapter.carteira;


import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.domain.repository.carteira.CotacaoRepository;
import com.wallet.md.infrastructure.persistence.repository.carteira.JpaCotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CotacaoRepositoryAdapter implements CotacaoRepository {

    private final JpaCotacaoRepository jpaRepository;

    @Override
    public Optional<BigDecimal> findLatestPrecoByAtivoId(Long ativoId) {
        return jpaRepository.findLatestPrecoByAtivoId(ativoId);
    }

    @Override
    public Cotacao findLatestByAtivoId(Long ativoId) {
        return null;
    }

    @Override
    public Optional<BigDecimal> findFirstOfMonthPrecoByAtivoId(Long ativoId, LocalDate mesAno) {
        return jpaRepository.findFirstOfMonthPrecoByAtivoId(ativoId, mesAno);
    }
}

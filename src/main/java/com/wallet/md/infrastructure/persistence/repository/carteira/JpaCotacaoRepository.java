package com.wallet.md.infrastructure.persistence.repository.carteira;

import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface JpaCotacaoRepository extends JpaRepository<Cotacao, Long> {
    // Cotação mais recente de um ativo
    Optional<Cotacao> findLatestByAtivoId(Long ativoId);

    @Query("SELECT c.preco FROM CotacaoEntity c WHERE c.ativoId = :ativoId ORDER BY c.data DESC LIMIT 1")
    Optional<BigDecimal> findLatestPrecoByAtivoId(@Param("ativoId") Long ativoId);

    @Query("SELECT c.preco FROM CotacaoEntity c WHERE c.ativoId = :ativoId AND c.data >= :mesAno ORDER BY c.data ASC LIMIT 1")
    Optional<BigDecimal> findFirstOfMonthPrecoByAtivoId(@Param("ativoId") Long ativoId, @Param("mesAno") LocalDate mesAno);
}
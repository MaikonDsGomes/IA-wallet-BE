package com.wallet.md.infrastructure.persistence.repository.carteira;

import com.wallet.md.infrastructure.persistence.entity.CotacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface JpaCotacaoRepository extends JpaRepository<CotacaoEntity, Long> {

    @Query("SELECT c FROM CotacaoEntity c WHERE c.ativo.id = :ativoId ORDER BY c.coletadoEm DESC LIMIT 1")
    Optional<CotacaoEntity> findLatestByAtivoId(@Param("ativoId") Long ativoId);

    @Query("SELECT c.preco FROM CotacaoEntity c WHERE c.ativo.id = :ativoId ORDER BY c.coletadoEm DESC LIMIT 1")
    Optional<BigDecimal> findLatestPrecoByAtivoId(@Param("ativoId") Long ativoId);

    @Query("SELECT c.preco FROM CotacaoEntity c WHERE c.ativo.id = :ativoId AND c.coletadoEm >= :mesAno ORDER BY c.coletadoEm ASC LIMIT 1")
    Optional<BigDecimal> findFirstOfMonthPrecoByAtivoId(@Param("ativoId") Long ativoId, @Param("mesAno") LocalDate mesAno);
}
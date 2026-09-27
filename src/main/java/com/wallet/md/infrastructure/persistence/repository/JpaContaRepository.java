package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaContaRepository extends JpaRepository<ContaEntity, Long> {
    Optional<ContaEntity> findByUsuarioId(Long usuarioId);

    @Modifying
    @Query("update ContaEntity c set c.saldo = c.saldo + :valor where c.id = :contaId")
    int creditar(@Param("contaId") Long contaId, @Param("valor") java.math.BigDecimal valor);

    @Modifying
    @Query("update ContaEntity c set c.saldo = c.saldo - :valor where c.id = :contaId and c.saldo >= :valor")
    int debitar(@Param("contaId") Long contaId, @Param("valor") java.math.BigDecimal valor);
}

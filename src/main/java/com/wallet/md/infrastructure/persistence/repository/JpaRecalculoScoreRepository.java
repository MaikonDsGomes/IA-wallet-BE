package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.RecalculoScoreEntity;
import com.wallet.md.domain.model.enums.StatusRecalculoScore;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaRecalculoScoreRepository extends JpaRepository<RecalculoScoreEntity, Long> {

    Optional<RecalculoScoreEntity> findByCarteiraId(Long carteiraId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RecalculoScoreEntity r where r.id = :id")
    Optional<RecalculoScoreEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select r.id from RecalculoScoreEntity r
            where (r.status = :pendente and r.agendadoPara <= :agora)
               or (r.status = :processando and r.atualizadoEm <= :limiteProcessamento)
            order by r.agendadoPara asc
            """)
    List<Long> findIdsVencidos(
            @Param("agora") LocalDateTime agora,
            @Param("limiteProcessamento") LocalDateTime limiteProcessamento,
            @Param("pendente") StatusRecalculoScore pendente,
            @Param("processando") StatusRecalculoScore processando);
}

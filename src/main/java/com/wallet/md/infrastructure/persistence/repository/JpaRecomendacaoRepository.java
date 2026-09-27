package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.RecomendacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaRecomendacaoRepository extends JpaRepository<RecomendacaoEntity, Long> {
    List<RecomendacaoEntity> findByCarteiraIdAndAtivaTrueOrderByPrioridadeAscGeradaEmDesc(Long carteiraId);

    @Modifying
    @Query("update RecomendacaoEntity r set r.ativa = false where r.carteira.id = :carteiraId and r.ativa = true")
    int desativarAtivas(@Param("carteiraId") Long carteiraId);
}

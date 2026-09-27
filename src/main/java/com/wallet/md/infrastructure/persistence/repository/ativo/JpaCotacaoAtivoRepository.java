package com.wallet.md.infrastructure.persistence.repository.ativo;

import com.wallet.md.infrastructure.persistence.entity.CotacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCotacaoAtivoRepository extends JpaRepository<CotacaoEntity, Long> {
    Optional<CotacaoEntity> findFirstByAtivoIdOrderByColetadoEmDesc(Long ativoId);

    List<CotacaoEntity> findByAtivoIdOrderByColetadoEmDesc(Long ativoId);
}

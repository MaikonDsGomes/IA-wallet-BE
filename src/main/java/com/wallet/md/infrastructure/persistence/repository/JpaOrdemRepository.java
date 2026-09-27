package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.OrdemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaOrdemRepository extends JpaRepository<OrdemEntity, Long> {
    List<OrdemEntity> findByContaIdOrderByCriadaEmAscIdAsc(Long contaId);
}

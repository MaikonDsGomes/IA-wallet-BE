package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaMovimentacaoRepository extends JpaRepository<MovimentacaoEntity, Long> {
    List<MovimentacaoEntity> findByContaIdOrderByDataAscIdAsc(Long contaId);
}

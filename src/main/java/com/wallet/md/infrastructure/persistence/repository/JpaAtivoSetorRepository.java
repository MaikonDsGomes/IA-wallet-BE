package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.AtivoSetorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaAtivoSetorRepository extends JpaRepository<AtivoSetorEntity, Long> {
    Optional<AtivoSetorEntity> findByAtivoId(Long ativoId);
    List<AtivoSetorEntity> findByAtivoIdIn(List<Long> ativoIds);
}

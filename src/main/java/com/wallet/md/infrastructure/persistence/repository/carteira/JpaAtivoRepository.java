package com.wallet.md.infrastructure.persistence.repository.carteira;

import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAtivoRepository extends JpaRepository<AtivoEntity, Long> {
    Optional<AtivoEntity> findByCodigo(String codigo);
}
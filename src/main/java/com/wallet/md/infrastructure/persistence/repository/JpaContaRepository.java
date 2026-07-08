package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaContaRepository extends JpaRepository<ContaEntity, Long> {
    Optional<ContaEntity> findByUsuarioId(Long usuarioId);
}

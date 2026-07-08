package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.PerfilRiscoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPerfilRiscoRepository extends JpaRepository<PerfilRiscoEntity, Long> {
    Optional<PerfilRiscoEntity> findByUsuarioId(Long usuarioId);
}
package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCarteiraRepository extends JpaRepository<CarteiraEntity, Long> {
    Optional<CarteiraEntity> findByContaId(Long contaId);
}

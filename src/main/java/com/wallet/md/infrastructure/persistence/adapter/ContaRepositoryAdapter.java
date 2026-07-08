package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.Conta;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.infrastructure.persistence.entity.ContaEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// infrastructure/persistence/adapter/ContaRepositoryAdapter.java
@Repository
@RequiredArgsConstructor
public class ContaRepositoryAdapter implements ContaRepository {

    private final JpaContaRepository jpaRepository;

    @Override
    public Optional<Conta> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).map(this::toDomain);
    }

    private Conta toDomain(ContaEntity entity) {
        return Conta.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario().getId())
                .saldo(entity.getSaldo())
                .margemDisponivel(entity.getMargemDisponivel())
                .build();
    }
}

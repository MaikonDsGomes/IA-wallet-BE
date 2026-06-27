package com.wallet.md.infrastructure.persistence.adapter.carteira;

import com.wallet.md.domain.model.Ativo;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.repository.carteira.AtivoRepository;
import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import com.wallet.md.infrastructure.persistence.repository.carteira.JpaAtivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AtivoRepositoryAdapter implements AtivoRepository {

    private final JpaAtivoRepository jpaRepository;

    @Override
    public Optional<Ativo> findByCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(this::toDomain);
    }

    private Ativo toDomain(AtivoEntity entity) {
        return Ativo.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .codigo(entity.getCodigo())
                .tipo(entity.getTipo())
                .volatilidade(entity.getVolatilidade())
                .ativo(entity.getAtivo())
                .build();
    }
}
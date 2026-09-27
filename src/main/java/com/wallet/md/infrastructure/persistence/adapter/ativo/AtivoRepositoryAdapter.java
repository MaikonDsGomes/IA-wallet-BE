package com.wallet.md.infrastructure.persistence.adapter.ativo;

import com.wallet.md.domain.repository.ativo.AtivoRepository;
import com.wallet.md.domain.model.Ativo;
import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import com.wallet.md.infrastructure.persistence.repository.ativo.JpaCatalogoAtivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.wallet.md.domain.model.enums.TipoAtivo;
import java.util.List;
import java.util.Optional;

@Repository("catalogoAtivoRepositoryAdapter")
@RequiredArgsConstructor
public class AtivoRepositoryAdapter implements AtivoRepository {

    private final JpaCatalogoAtivoRepository jpaRepository;

    @Override
    public List<Ativo> findAtivos(TipoAtivo tipo, String busca) {
        return jpaRepository.findAtivos(tipo, busca).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Ativo> findByCodigo(String codigo) {
        return jpaRepository.findByCodigoIgnoreCaseAndAtivoTrue(codigo)
                .map(this::toDomain);
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

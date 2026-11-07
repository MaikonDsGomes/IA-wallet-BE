package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.AtivoSetor;
import com.wallet.md.domain.repository.mercado.AtivoSetorRepository;
import com.wallet.md.infrastructure.persistence.repository.JpaAtivoSetorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AtivoSetorRepositoryAdapter implements AtivoSetorRepository {

    private final JpaAtivoSetorRepository repository;

    @Override
    public Optional<AtivoSetor> findByAtivoId(Long ativoId) {
        return repository.findByAtivoId(ativoId).map(entity -> AtivoSetor.builder()
                .ativoId(entity.getAtivo().getId())
                .setor(entity.getSetor())
                .fonte(entity.getFonte())
                .atualizadoEm(entity.getAtualizadoEm())
                .build());
    }

    @Override
    public List<AtivoSetor> findByAtivoIds(List<Long> ativoIds) {
        return repository.findByAtivoIdIn(ativoIds).stream()
                .map(entity -> AtivoSetor.builder()
                        .ativoId(entity.getAtivo().getId())
                        .setor(entity.getSetor())
                        .fonte(entity.getFonte())
                        .atualizadoEm(entity.getAtualizadoEm())
                        .build())
                .toList();
    }
}

package com.wallet.md.infrastructure.persistence.adapter;


import com.wallet.md.domain.model.PerfilRisco;
import com.wallet.md.domain.repository.carteira.PerfilRiscoRepository;
import com.wallet.md.infrastructure.persistence.entity.PerfilRiscoEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaPerfilRiscoRepository;
import com.wallet.md.infrastructure.persistence.repository.JpaUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PerfilRiscoRepositoryAdapter implements PerfilRiscoRepository {

    private final JpaPerfilRiscoRepository jpaRepository;
    private final JpaUsuarioRepository jpaUsuarioRepository;

    @Override
    public Optional<PerfilRisco> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).map(this::toDomain);
    }

    @Override
    public PerfilRisco createIfAbsent(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId)
                .map(this::toDomain)
                .orElseGet(() -> toDomain(jpaRepository.save(PerfilRiscoEntity.builder()
                        .usuario(jpaUsuarioRepository.getReferenceById(usuarioId))
                        .score(0)
                        .nivel(com.wallet.md.domain.model.enums.NivelRisco.MODERADO)
                        .margemMinima(0)
                        .build())));
    }

    private PerfilRisco toDomain(PerfilRiscoEntity entity) {
        return PerfilRisco.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario().getId())
                .score(entity.getScore())
                .nivel(entity.getNivel())
                .margemMinima(entity.getMargemMinima())
                .calculadoEm(entity.getCalculadoEm())
                .build();
    }
}

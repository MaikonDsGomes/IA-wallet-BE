package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.RecalculoScore;
import com.wallet.md.domain.repository.carteira.RecalculoScoreRepository;
import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import com.wallet.md.infrastructure.persistence.entity.RecalculoScoreEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaRecalculoScoreRepository;
import com.wallet.md.domain.model.enums.StatusRecalculoScore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecalculoScoreRepositoryAdapter implements RecalculoScoreRepository {

    private final JpaRecalculoScoreRepository jpaRepository;

    @Override
    public Optional<RecalculoScore> findByCarteiraId(Long carteiraId) {
        return jpaRepository.findByCarteiraId(carteiraId).map(this::toDomain);
    }

    @Override
    public Optional<RecalculoScore> findByIdForUpdate(Long id) {
        return jpaRepository.findByIdForUpdate(id).map(this::toDomain);
    }

    @Override
    public List<Long> findIdsVencidos(LocalDateTime agora, LocalDateTime limiteProcessamento) {
        return jpaRepository.findIdsVencidos(
                agora, limiteProcessamento,
                StatusRecalculoScore.PENDENTE, StatusRecalculoScore.PROCESSANDO);
    }

    @Override
    public RecalculoScore save(RecalculoScore recalculo) {
        var carteira = new CarteiraEntity();
        carteira.setId(recalculo.getCarteiraId());
        var entity = RecalculoScoreEntity.builder()
                .id(recalculo.getId())
                .carteira(carteira)
                .agendadoPara(recalculo.getAgendadoPara())
                .status(recalculo.getStatus())
                .tentativas(recalculo.getTentativas())
                .versaoAlteracao(recalculo.getVersaoAlteracao())
                .ultimoErro(recalculo.getUltimoErro())
                .processadoEm(recalculo.getProcessadoEm())
                .criadoEm(recalculo.getCriadoEm())
                .atualizadoEm(recalculo.getAtualizadoEm())
                .version(recalculo.getVersion())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    private RecalculoScore toDomain(RecalculoScoreEntity entity) {
        return RecalculoScore.builder()
                .id(entity.getId())
                .carteiraId(entity.getCarteira().getId())
                .agendadoPara(entity.getAgendadoPara())
                .status(entity.getStatus())
                .tentativas(entity.getTentativas())
                .versaoAlteracao(entity.getVersaoAlteracao())
                .ultimoErro(entity.getUltimoErro())
                .processadoEm(entity.getProcessadoEm())
                .criadoEm(entity.getCriadoEm())
                .atualizadoEm(entity.getAtualizadoEm())
                .version(entity.getVersion())
                .build();
    }
}

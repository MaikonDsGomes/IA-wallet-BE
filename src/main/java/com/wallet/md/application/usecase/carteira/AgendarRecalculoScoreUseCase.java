package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.model.RecalculoScore;
import com.wallet.md.domain.model.enums.StatusRecalculoScore;
import com.wallet.md.domain.repository.carteira.RecalculoScoreRepository;
import com.wallet.md.infrastructure.config.ScoreMetodologiaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AgendarRecalculoScoreUseCase {

    private final RecalculoScoreRepository repository;
    private final ScoreMetodologiaProperties properties;

    @Transactional
    public void agendar(Long carteiraId) {
        var agora = LocalDateTime.now();
        var recalculo = repository.findByCarteiraId(carteiraId)
                .orElseGet(() -> RecalculoScore.builder()
                        .carteiraId(carteiraId)
                        .tentativas(0)
                        .versaoAlteracao(0)
                        .criadoEm(agora)
                        .build());
        recalculo.setAgendadoPara(agora.plusMinutes(properties.getDebounceMinutos()));
        recalculo.setStatus(StatusRecalculoScore.PENDENTE);
        recalculo.setTentativas(0);
        recalculo.setVersaoAlteracao(recalculo.getVersaoAlteracao() + 1);
        recalculo.setUltimoErro(null);
        recalculo.setProcessadoEm(null);
        recalculo.setAtualizadoEm(agora);
        repository.save(recalculo);
    }
}

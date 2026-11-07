package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.event.ScoreRecalculatedEvent;
import com.wallet.md.domain.model.enums.StatusRecalculoScore;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.RecalculoScoreRepository;
import com.wallet.md.infrastructure.config.ScoreMetodologiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class ProcessarRecalculoScoreUseCase {

    private final RecalculoScoreRepository recalculoRepository;
    private final CarteiraRepository carteiraRepository;
    private final CalcularScoreCarteiraUseCase calcularScoreCarteiraUseCase;
    private final GerarRecomendacoesCarteiraUseCase gerarRecomendacoesCarteiraUseCase;
    private final ScoreMetodologiaProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processar(Long recalculoId) {
        var recalculo = recalculoRepository.findByIdForUpdate(recalculoId).orElse(null);
        if (recalculo == null || !podeProcessar(recalculo.getStatus())) {
            return;
        }

        var versao = recalculo.getVersaoAlteracao();
        recalculo.setStatus(StatusRecalculoScore.PROCESSANDO);
        recalculo.setTentativas(recalculo.getTentativas() + 1);
        recalculo.setUltimoErro(null);
        recalculoRepository.save(recalculo);

        try {
            calcularScoreCarteiraUseCase.calcular(recalculo.getCarteiraId());
            var atualizado = recalculoRepository.findByIdForUpdate(recalculoId).orElseThrow();
            if (!versao.equals(atualizado.getVersaoAlteracao())) {
                atualizado.setStatus(StatusRecalculoScore.PENDENTE);
                atualizado.setAgendadoPara(LocalDateTime.now().plusMinutes(properties.getDebounceMinutos()));
                recalculoRepository.save(atualizado);
                return;
            }

            carteiraRepository.findUsuarioIdByCarteiraId(recalculo.getCarteiraId())
                    .ifPresent(usuarioId -> {
                        try {
                            gerarRecomendacoesCarteiraUseCase.gerar(usuarioId);
                        } catch (RuntimeException exception) {
                            log.warn("Score salvo, mas recomendações não foram atualizadas para carteira {}: {}",
                                    recalculo.getCarteiraId(), exception.getMessage());
                        }
                    });

            atualizado.setStatus(StatusRecalculoScore.CONCLUIDO);
            atualizado.setProcessadoEm(LocalDateTime.now());
            recalculoRepository.save(atualizado);
            eventPublisher.publishEvent(new ScoreRecalculatedEvent(
                    atualizado.getId(), atualizado.getCarteiraId()));
        } catch (RuntimeException exception) {
            var atual = recalculoRepository.findByIdForUpdate(recalculoId).orElseThrow();
            atual.setUltimoErro(exception.getMessage());
            if (atual.getTentativas() >= properties.getMaxTentativas()) {
                atual.setStatus(StatusRecalculoScore.FALHO);
            } else {
                atual.setStatus(StatusRecalculoScore.PENDENTE);
                atual.setAgendadoPara(LocalDateTime.now().plusMinutes(properties.getDebounceMinutos()));
            }
            recalculoRepository.save(atual);
            log.error("Falha ao recalcular score da carteira {}", atual.getCarteiraId(), exception);
        }
    }

    private boolean podeProcessar(StatusRecalculoScore status) {
        return status == StatusRecalculoScore.PENDENTE
                || status == StatusRecalculoScore.PROCESSANDO;
    }
}

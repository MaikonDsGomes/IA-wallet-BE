package com.wallet.md.application.event;

import com.wallet.md.application.usecase.notification.NotifyScoreRecalculatedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ScoreRecalculatedEventListener {

    private final NotifyScoreRecalculatedUseCase notifyScoreRecalculatedUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ScoreRecalculatedEvent event) {
        notifyScoreRecalculatedUseCase.execute(event.recalculoId(), event.carteiraId());
    }
}

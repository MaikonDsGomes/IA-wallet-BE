package com.wallet.md.application.usecase.notification;

import com.wallet.md.domain.model.ScoreEmailNotification;
import com.wallet.md.domain.model.enums.StatusScoreEmailNotification;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.notification.ScoreEmailNotificationRepository;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class NotifyScoreRecalculatedUseCase {

    private static final String SUBJECT = "Seu score de carteira foi atualizado!";

    private final ScoreEmailNotificationRepository notificationRepository;
    private final CarteiraRepository carteiraRepository;
    private final UsuarioRepository usuarioRepository;
    private final SendEmailUseCase sendEmailUseCase;

    @Transactional
    public void execute(Long recalculoId, Long carteiraId) {
        var notification = notificationRepository.findByRecalculoIdForUpdate(recalculoId)
                .orElseGet(() -> notificationRepository.save(ScoreEmailNotification.builder()
                        .recalculoId(recalculoId)
                        .status(StatusScoreEmailNotification.PROCESSANDO)
                        .tentativas(0)
                        .build()));

        if (notification.getStatus() == StatusScoreEmailNotification.ENVIADO) {
            return;
        }

        notification.setStatus(StatusScoreEmailNotification.PROCESSANDO);
        notification.setTentativas(notification.getTentativas() + 1);
        notificationRepository.save(notification);

        try {
            var usuarioId = carteiraRepository.findUsuarioIdByCarteiraId(carteiraId).orElseThrow(
                    () -> new IllegalStateException("Proprietário da carteira não encontrado"));
            var usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                    () -> new IllegalStateException("Usuário proprietário da carteira não encontrado"));

            var body = """
                    Olá, %s!

                    O score da sua carteira de investimentos foi atualizado com sucesso.

                    Você já pode consultar a nova pontuação e os indicadores de diversificação, gestão de risco, concentração e performance no Dashboard do InvestWallet.

                    Acesse sua conta para conferir os detalhes e acompanhar a evolução da carteira.

                    InvestWallet — acompanhe seus investimentos com inteligência.
                    """.formatted(usuario.getNome());

            sendEmailUseCase.sendEmail(usuario.getEmail(), SUBJECT, body);
            notification.setStatus(StatusScoreEmailNotification.ENVIADO);
            notification.setEnviadoEm(LocalDateTime.now());
            notification.setUltimoErro(null);
            notificationRepository.save(notification);
        } catch (RuntimeException exception) {
            notification.setStatus(StatusScoreEmailNotification.FALHO);
            notification.setUltimoErro(resumirErro(exception));
            notificationRepository.save(notification);
            log.warn("Falha na notificação de score: carteira={}, processamento={}, tipo={}",
                    carteiraId, recalculoId, exception.getClass().getSimpleName());
        }
    }

    private String resumirErro(RuntimeException exception) {
        var message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return message.length() > 900 ? message.substring(0, 900) : message;
    }
}

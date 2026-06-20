package com.wallet.md.application.usecase.usuario;

import com.wallet.md.domain.repository.usuario.TokenBlacklistRepository;
import com.wallet.md.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutUseCase {

    private final TokenBlacklistRepository blacklistRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void execute(String token) {
        long expirationMs = jwtTokenProvider.getTimeUntilExpiration(token);
        blacklistRepository.blacklist(token, expirationMs);
    }


}

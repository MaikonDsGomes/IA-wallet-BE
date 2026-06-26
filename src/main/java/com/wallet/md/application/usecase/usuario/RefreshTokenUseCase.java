package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.request.RefreshTokenRequest;
import com.wallet.md.application.dto.response.AuthResponse;
import com.wallet.md.domain.exception.InvalidCredentialsException;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.domain.service.TokenProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final TokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    public AuthResponse execute(RefreshTokenRequest request) {
        if (!tokenProvider.isRefreshToken(request.refreshToken())) {
            throw new InvalidCredentialsException();
        }

        var userId = tokenProvider.validateTokenAndGetUserId(request.refreshToken())
                .orElseThrow(InvalidCredentialsException::new);

        var usuario = usuarioRepository.findById(userId)
                .orElseThrow(InvalidCredentialsException::new);

        if (!usuario.getStatus()) {
            throw new InvalidCredentialsException();
        }

        var accessToken = tokenProvider.generateAccessToken(usuario);
        var refreshToken = tokenProvider.generateRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken, tokenProvider.getAccessTokenExpirationMs());
    }
}

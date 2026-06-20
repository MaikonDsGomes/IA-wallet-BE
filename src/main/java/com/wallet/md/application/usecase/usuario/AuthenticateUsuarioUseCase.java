package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.request.LoginRequest;
import com.wallet.md.application.dto.response.AuthResponse;
import com.wallet.md.domain.exception.InvalidCredentialsException;
import com.wallet.md.domain.repository.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import com.wallet.md.domain.service.TokenProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderService passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthResponse execute(LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!usuario.getStatus()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new InvalidCredentialsException();
        }

        var accessToken = tokenProvider.generateAccessToken(usuario);
        var refreshToken = tokenProvider.generateRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken, tokenProvider.getAccessTokenExpirationMs());
    }
}

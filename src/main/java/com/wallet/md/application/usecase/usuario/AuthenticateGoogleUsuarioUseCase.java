package com.wallet.md.application.usecase.usuario;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.wallet.md.application.dto.request.GoogleAuthRequest;
import com.wallet.md.application.dto.response.AuthResponse;
import com.wallet.md.domain.exception.InvalidCredentialsException;
import com.wallet.md.domain.model.Usuario;
import com.wallet.md.domain.model.enums.Role;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import com.wallet.md.domain.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthenticateGoogleUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderService passwordEncoder;
    private final TokenProvider tokenProvider;

    @Value("${google.client-id}")
    private String googleClientId;

    public AuthResponse execute(GoogleAuthRequest request) {
        GoogleIdToken.Payload payload = verify(request.credential());
        String email = payload.getEmail();

        var usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            var novoUsuario = Usuario.builder()
                    .nome(payload.get("name") != null ? payload.get("name").toString() : email)
                    .email(email)
                    .senha(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .status(true)
                    .build();
            return usuarioRepository.save(novoUsuario);
        });

        if (!Boolean.TRUE.equals(usuario.getStatus())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(
                tokenProvider.generateAccessToken(usuario),
                tokenProvider.generateRefreshToken(usuario),
                tokenProvider.getAccessTokenExpirationMs()
        );
    }

    private GoogleIdToken.Payload verify(String credential) {
        try {
            var verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(List.of(googleClientId)).build();

            var token = verifier.verify(credential);
            if (token == null || !Boolean.TRUE.equals(token.getPayload().getEmailVerified())) {
                throw new InvalidCredentialsException();
            }
            return token.getPayload();
        } catch (InvalidCredentialsException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InvalidCredentialsException();
        }
    }
}

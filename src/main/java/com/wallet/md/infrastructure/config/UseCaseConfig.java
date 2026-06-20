package com.wallet.md.infrastructure.config;

import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.application.usecase.*;
import com.wallet.md.application.usecase.usuario.*;
import com.wallet.md.domain.repository.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import com.wallet.md.domain.service.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUsuarioUseCase registerUsuarioUseCase(
            UsuarioRepository repo, PasswordEncoderService encoder, UsuarioMapper mapper) {
        return new RegisterUsuarioUseCase(repo, encoder, mapper);
    }

    @Bean
    public AuthenticateUsuarioUseCase authenticateUsuarioUseCase(
            UsuarioRepository repo, PasswordEncoderService encoder, TokenProvider tokenProvider) {
        return new AuthenticateUsuarioUseCase(repo, encoder, tokenProvider);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            TokenProvider tokenProvider, UsuarioRepository repo) {
        return new RefreshTokenUseCase(tokenProvider, repo);
    }

    @Bean
    public GetUsuarioUseCase getUsuarioUseCase(UsuarioRepository repo, UsuarioMapper mapper) {
        return new GetUsuarioUseCase(repo, mapper);
    }

    @Bean
    public ListUsuariosUseCase listUsuariosUseCase(UsuarioRepository repo, UsuarioMapper mapper) {
        return new ListUsuariosUseCase(repo, mapper);
    }

    @Bean
    public UpdateUsuarioUseCase updateUsuarioUseCase(UsuarioRepository repo, UsuarioMapper mapper) {
        return new UpdateUsuarioUseCase(repo, mapper);
    }

    @Bean
    public DeleteUsuarioUseCase deleteUsuarioUseCase(UsuarioRepository repo) {
        return new DeleteUsuarioUseCase(repo);
    }
}

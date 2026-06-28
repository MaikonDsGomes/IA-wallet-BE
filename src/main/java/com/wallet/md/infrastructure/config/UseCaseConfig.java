package com.wallet.md.infrastructure.config;

import com.wallet.md.application.dto.request.carteira.DeletePosicaoRequest;
import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.application.usecase.*;
import com.wallet.md.application.usecase.carteira.DeletePosicaoUseCase;
import com.wallet.md.application.usecase.carteira.PostPosicaoUseCase;
import com.wallet.md.application.usecase.carteira.GetPosicoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GetScoreCarteiraUseCase;
import com.wallet.md.application.usecase.usuario.*;
import com.wallet.md.domain.repository.carteira.*;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
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
    public PostPosicaoUseCase adicionarPosicaoUseCase(
            CarteiraRepository carteiraRepository,
            AtivoRepository ativoRepository,
            PosicaoRepository posicaoRepository) {
        return new PostPosicaoUseCase(carteiraRepository, ativoRepository, posicaoRepository);
    }

    @Bean
    public GetPosicoesCarteiraUseCase getPosicoesCarteiraUseCase(
            CarteiraRepository carteiraRepository,
            PosicaoRepository posicaoRepository,
            CotacaoRepository cotacaoRepository) {
        return new GetPosicoesCarteiraUseCase(carteiraRepository, posicaoRepository, cotacaoRepository);
    }
    @Bean
    public DeletePosicaoUseCase deletePosicaoUseCase(
            CarteiraRepository carteiraRepository,
            PosicaoRepository posicaoRepository) {
        return new DeletePosicaoUseCase(carteiraRepository, posicaoRepository);
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
    public UpdateUsuarioUseCase updateUsuarioUseCase(UsuarioRepository repo, UsuarioMapper mapper, PasswordEncoderService PasswordEncoderService) {
        return new UpdateUsuarioUseCase(repo, mapper, PasswordEncoderService);
    }

    @Bean
    public DeleteUsuarioUseCase deleteUsuarioUseCase(UsuarioRepository repo) {
        return new DeleteUsuarioUseCase(repo);
    }

    @Bean
    public GetScoreCarteiraUseCase getScoreCarteiraUseCase(ContaRepository contaRepository, CarteiraRepository carteiraRepository, ScoreCarteiraRepository scoreCarteiraRepository) {
        return new GetScoreCarteiraUseCase(contaRepository, carteiraRepository, scoreCarteiraRepository);
    }
}

package com.wallet.md.infrastructure.config;

import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.application.usecase.carteira.DeletePosicaoUseCase;
import com.wallet.md.application.usecase.carteira.PostPosicaoUseCase;
import com.wallet.md.application.usecase.carteira.GetPosicoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GetScoreCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GetRecomendacoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GerarRecomendacoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.SolicitarAtualizacaoRecomendacoesUseCase;
import com.wallet.md.application.usecase.carteira.VenderPosicaoUseCase;
import com.wallet.md.application.usecase.carteira.GetMovimentacoesContaUseCase;
import com.wallet.md.application.usecase.carteira.GetOrdensContaUseCase;
import com.wallet.md.application.usecase.carteira.ComprarPosicaoUseCase;
import com.wallet.md.application.usecase.carteira.GetCarteiraKpisUseCase;
import com.wallet.md.application.usecase.carteira.GetEvolucaoCarteiraUseCase;
import com.wallet.md.application.usecase.ativo.GetAtivosPorTicket;
import com.wallet.md.application.usecase.usuario.*;
import com.wallet.md.application.service.RecommendationTextProvider;
import com.wallet.md.domain.repository.carteira.*;
import com.wallet.md.domain.repository.ativo.CotacaoAtivoRepository;
import com.wallet.md.domain.repository.ativo.AtivoRepository;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import com.wallet.md.domain.service.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetAtivosPorTicket getAtivosPorTicket(
            AtivoRepository ativoRepository,
            CotacaoAtivoRepository cotacaoRepository) {
        return new GetAtivosPorTicket(ativoRepository, cotacaoRepository);
    }

    @Bean
    public RegisterUsuarioUseCase registerUsuarioUseCase(
            UsuarioRepository repo, PasswordEncoderService encoder, UsuarioMapper mapper) {
        return new RegisterUsuarioUseCase(repo, encoder, mapper);
    }

    @Bean
    public PostPosicaoUseCase adicionarPosicaoUseCase(
            CarteiraRepository carteiraRepository,
            com.wallet.md.domain.repository.carteira.AtivoRepository ativoRepository,
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
    public AuthenticateGoogleUsuarioUseCase authenticateGoogleUsuarioUseCase(
            UsuarioRepository repo, PasswordEncoderService encoder, TokenProvider tokenProvider) {
        return new AuthenticateGoogleUsuarioUseCase(repo, encoder, tokenProvider);
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

    @Bean
    public GetRecomendacoesCarteiraUseCase getRecomendacoesCarteiraUseCase(
            ContaRepository contaRepository,
            CarteiraRepository carteiraRepository,
            RecomendacaoRepository recomendacaoRepository) {
        return new GetRecomendacoesCarteiraUseCase(contaRepository, carteiraRepository, recomendacaoRepository);
    }

    @Bean
    public GerarRecomendacoesCarteiraUseCase gerarRecomendacoesCarteiraUseCase(
            ContaRepository contaRepository,
            CarteiraRepository carteiraRepository,
            PerfilRiscoRepository perfilRiscoRepository,
            ScoreCarteiraRepository scoreCarteiraRepository,
            RecomendacaoRepository recomendacaoRepository,
            RecommendationTextProvider recommendationTextProvider) {
        return new GerarRecomendacoesCarteiraUseCase(
                contaRepository, carteiraRepository, perfilRiscoRepository,
                scoreCarteiraRepository, recomendacaoRepository, recommendationTextProvider);
    }

    @Bean
    public SolicitarAtualizacaoRecomendacoesUseCase solicitarAtualizacaoRecomendacoesUseCase(
            GerarRecomendacoesCarteiraUseCase gerarRecomendacoesCarteiraUseCase) {
        return new SolicitarAtualizacaoRecomendacoesUseCase(gerarRecomendacoesCarteiraUseCase);
    }

    @Bean
    public VenderPosicaoUseCase venderPosicaoUseCase(
            ContaRepository contaRepository,
            CarteiraRepository carteiraRepository,
            PosicaoRepository posicaoRepository,
            OrdemRepository ordemRepository,
            MovimentacaoRepository movimentacaoRepository) {
        return new VenderPosicaoUseCase(contaRepository, carteiraRepository, posicaoRepository,
                ordemRepository, movimentacaoRepository);
    }

    @Bean
    public GetMovimentacoesContaUseCase getMovimentacoesContaUseCase(
            ContaRepository contaRepository, MovimentacaoRepository movimentacaoRepository) {
        return new GetMovimentacoesContaUseCase(contaRepository, movimentacaoRepository);
    }

    @Bean
    public GetOrdensContaUseCase getOrdensContaUseCase(
            ContaRepository contaRepository, OrdemRepository ordemRepository) {
        return new GetOrdensContaUseCase(contaRepository, ordemRepository);
    }

    @Bean
    public ComprarPosicaoUseCase comprarPosicaoUseCase(
            ContaRepository contaRepository,
            CarteiraRepository carteiraRepository,
            PosicaoRepository posicaoRepository,
            OrdemRepository ordemRepository,
            MovimentacaoRepository movimentacaoRepository) {
        return new ComprarPosicaoUseCase(contaRepository, carteiraRepository, posicaoRepository,
                ordemRepository, movimentacaoRepository);
    }

    @Bean
    public GetCarteiraKpisUseCase getCarteiraKpisUseCase(
            CarteiraRepository carteiraRepository, PosicaoRepository posicaoRepository,
            CotacaoRepository cotacaoRepository) {
        return new GetCarteiraKpisUseCase(carteiraRepository, posicaoRepository, cotacaoRepository);
    }

    @Bean
    public GetEvolucaoCarteiraUseCase getEvolucaoCarteiraUseCase(
            ContaRepository contaRepository, MovimentacaoRepository movimentacaoRepository) {
        return new GetEvolucaoCarteiraUseCase(contaRepository, movimentacaoRepository);
    }
}

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
import com.wallet.md.application.usecase.carteira.CalcularScoreCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.AgendarRecalculoScoreUseCase;
import com.wallet.md.application.usecase.carteira.ProcessarRecalculoScoreUseCase;
import com.wallet.md.application.usecase.mercado.IngerirDadosMercadoUseCase;
import com.wallet.md.application.usecase.notification.SendEmailUseCase;
import com.wallet.md.application.usecase.ativo.GetAtivosPorTicket;
import com.wallet.md.application.usecase.usuario.*;
import com.wallet.md.domain.repository.carteira.*;
import com.wallet.md.domain.repository.mercado.AtivoSetorRepository;
import com.wallet.md.domain.repository.mercado.MarketSeriesRepository;
import com.wallet.md.domain.repository.mercado.MarketDataProvider;
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
            UsuarioRepository repo, PasswordEncoderService encoder, UsuarioMapper mapper,
            ProvisionarCarteiraUsuarioUseCase provisionarCarteiraUsuarioUseCase) {
        return new RegisterUsuarioUseCase(repo, encoder, mapper, provisionarCarteiraUsuarioUseCase);
    }

    @Bean
    public ProvisionarCarteiraUsuarioUseCase provisionarCarteiraUsuarioUseCase(
            ContaRepository contaRepository,
            CarteiraRepository carteiraRepository,
            PerfilRiscoRepository perfilRiscoRepository) {
        return new ProvisionarCarteiraUsuarioUseCase(contaRepository, carteiraRepository, perfilRiscoRepository);
    }

    @Bean
    public PostPosicaoUseCase adicionarPosicaoUseCase(
            CarteiraRepository carteiraRepository,
            com.wallet.md.domain.repository.carteira.AtivoRepository ativoRepository,
            PosicaoRepository posicaoRepository,
            AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase) {
        return new PostPosicaoUseCase(carteiraRepository, ativoRepository, posicaoRepository,
                agendarRecalculoScoreUseCase);
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
            PosicaoRepository posicaoRepository,
            AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase) {
        return new DeletePosicaoUseCase(carteiraRepository, posicaoRepository, agendarRecalculoScoreUseCase);
    }
    @Bean
    public AuthenticateUsuarioUseCase authenticateUsuarioUseCase(
            UsuarioRepository repo, PasswordEncoderService encoder, TokenProvider tokenProvider) {
        return new AuthenticateUsuarioUseCase(repo, encoder, tokenProvider);
    }

    @Bean
    public AuthenticateGoogleUsuarioUseCase authenticateGoogleUsuarioUseCase(
            UsuarioRepository repo, PasswordEncoderService encoder, TokenProvider tokenProvider,
            ProvisionarCarteiraUsuarioUseCase provisionarCarteiraUsuarioUseCase) {
        return new AuthenticateGoogleUsuarioUseCase(repo, encoder, tokenProvider, provisionarCarteiraUsuarioUseCase);
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
    public GetScoreCarteiraUseCase getScoreCarteiraUseCase(
            ContaRepository contaRepository,
            CarteiraRepository carteiraRepository,
            ScoreCarteiraRepository scoreCarteiraRepository,
            com.wallet.md.infrastructure.config.ScoreMetodologiaProperties properties) {
        return new GetScoreCarteiraUseCase(contaRepository, carteiraRepository, scoreCarteiraRepository, properties);
    }

    @Bean
    public CalcularScoreCarteiraUseCase calcularScoreCarteiraUseCase(
            CarteiraRepository carteiraRepository,
            PosicaoRepository posicaoRepository,
            CotacaoRepository cotacaoRepository,
            PerfilRiscoRepository perfilRiscoRepository,
            ScoreCarteiraRepository scoreCarteiraRepository,
            com.wallet.md.infrastructure.config.ScoreMetodologiaProperties properties,
            MarketSeriesRepository marketSeriesRepository,
            AtivoSetorRepository ativoSetorRepository) {
        return new CalcularScoreCarteiraUseCase(carteiraRepository, posicaoRepository, cotacaoRepository,
                perfilRiscoRepository, scoreCarteiraRepository, properties, marketSeriesRepository,
                ativoSetorRepository);
    }

    @Bean
    public AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase(
            RecalculoScoreRepository recalculoScoreRepository,
            com.wallet.md.infrastructure.config.ScoreMetodologiaProperties properties) {
        return new AgendarRecalculoScoreUseCase(recalculoScoreRepository, properties);
    }

    @Bean
    public ProcessarRecalculoScoreUseCase processarRecalculoScoreUseCase(
            RecalculoScoreRepository recalculoScoreRepository,
            CarteiraRepository carteiraRepository,
            CalcularScoreCarteiraUseCase calcularScoreCarteiraUseCase,
            GerarRecomendacoesCarteiraUseCase gerarRecomendacoesCarteiraUseCase,
            com.wallet.md.infrastructure.config.ScoreMetodologiaProperties properties) {
        return new ProcessarRecalculoScoreUseCase(recalculoScoreRepository, carteiraRepository,
                calcularScoreCarteiraUseCase, gerarRecomendacoesCarteiraUseCase, properties);
    }

    @Bean
    public IngerirDadosMercadoUseCase ingerirDadosMercadoUseCase(
            PosicaoRepository posicaoRepository,
            MarketDataProvider marketDataProvider,
            MarketSeriesRepository marketSeriesRepository,
            MarketDataProperties marketDataProperties) {
        return new IngerirDadosMercadoUseCase(posicaoRepository, marketDataProvider,
                marketSeriesRepository, marketDataProperties);
    }

    @Bean
    public SendEmailUseCase sendEmailUseCase(
            com.wallet.md.domain.repository.notification.EmailSenderPort emailSenderPort) {
        return new SendEmailUseCase(emailSenderPort);
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
            RecomendacaoRepository recomendacaoRepository) {
        return new GerarRecomendacoesCarteiraUseCase(
                contaRepository, carteiraRepository, perfilRiscoRepository,
                scoreCarteiraRepository, recomendacaoRepository);
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
            MovimentacaoRepository movimentacaoRepository,
            AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase) {
        return new VenderPosicaoUseCase(contaRepository, carteiraRepository, posicaoRepository,
                ordemRepository, movimentacaoRepository, agendarRecalculoScoreUseCase);
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
            MovimentacaoRepository movimentacaoRepository,
            AgendarRecalculoScoreUseCase agendarRecalculoScoreUseCase) {
        return new ComprarPosicaoUseCase(contaRepository, carteiraRepository, posicaoRepository,
                ordemRepository, movimentacaoRepository, agendarRecalculoScoreUseCase);
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

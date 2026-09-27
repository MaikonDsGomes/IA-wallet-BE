package com.wallet.md.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.md.application.dto.response.carteira.ScoreCarteiraResponse;
import com.wallet.md.application.usecase.carteira.*;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.infrastructure.security.JwtAuthenticationEntryPoint;
import com.wallet.md.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarteiraController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "1")
class CarteiraControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean GetScoreCarteiraUseCase scoreUseCase;
    @MockBean GetPosicoesCarteiraUseCase posicoesUseCase;
    @MockBean PostPosicaoUseCase postUseCase;
    @MockBean DeletePosicaoUseCase deleteUseCase;
    @MockBean GetRecomendacoesCarteiraUseCase recomendacoesUseCase;
    @MockBean SolicitarAtualizacaoRecomendacoesUseCase solicitarRecomendacoesUseCase;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private org.springframework.test.web.servlet.request.RequestPostProcessor user() {
        return SecurityMockMvcRequestPostProcessors.user("1");
    }

    private ScoreCarteiraResponse score() {
        return new ScoreCarteiraResponse(80, "Bom", 80, 5, 3,
                new BigDecimal("30"), 75, "Media", new BigDecimal("1"),
                new BigDecimal("2"), 70, new BigDecimal("60"),
                new BigDecimal("0.2"), 4, 80, new BigDecimal("3"),
                new BigDecimal("2"), new BigDecimal("1.5"), LocalDateTime.now());
    }

    @Test
    void deveConsultarScore() throws Exception {
        given(scoreUseCase.getScore(1L)).willReturn(score());
        mockMvc.perform(get("/api/v1/carteira/score").with(user()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.scoreTotal").value(80));
    }

    @Test
    void deveRetornarErroAoConsultarScore() throws Exception {
        given(scoreUseCase.getScore(1L)).willThrow(new ResourceNotFoundException("Score", 1));
        mockMvc.perform(get("/api/v1/carteira/score").with(user()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveConsultarRecomendacoes() throws Exception {
        given(recomendacoesUseCase.getRecomendacoes(1L)).willReturn(List.of());
        mockMvc.perform(get("/api/v1/carteira/recomendacoes").with(user()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deveAgendarAtualizacaoDeRecomendacoes() throws Exception {
        mockMvc.perform(post("/api/v1/carteira/recomendacoes/atualizacao").with(user()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Atualização de recomendações agendada"));
    }

    @Test
    void deveAdicionarPosicao() throws Exception {
        mockMvc.perform(post("/api/v1/carteira/posicoes").with(user())
                        .contentType("application/json")
                        .content("""
                                {"codigo":"PETR4","tipo":"ACAO","quantidade":10,"precoMedio":32.40}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRejeitarPosicaoInvalida() throws Exception {
        mockMvc.perform(post("/api/v1/carteira/posicoes").with(user())
                        .contentType("application/json")
                        .content("{\"codigo\":\"\",\"quantidade\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarPosicoes() throws Exception {
        given(posicoesUseCase.getDadosPosicao(1L, null)).willReturn(List.of());
        mockMvc.perform(get("/api/v1/carteira/posicoes").with(user()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deveFalharListarPosicoes() throws Exception {
        given(posicoesUseCase.getDadosPosicao(1L, null))
                .willThrow(new ResourceNotFoundException("Carteira", 1));
        mockMvc.perform(get("/api/v1/carteira/posicoes").with(user()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExcluirPosicao() throws Exception {
        mockMvc.perform(delete("/api/v1/carteira/posicoes").with(user())
                        .contentType("application/json").content("{\"ativoId\":1}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRejeitarExclusaoSemAtivo() throws Exception {
        mockMvc.perform(delete("/api/v1/carteira/posicoes").with(user())
                        .contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}

package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.response.ativo.AtivoResponse;
import com.wallet.md.application.dto.response.ativo.CotacaoResponse;
import com.wallet.md.application.usecase.ativo.GetAtivosPorTicket;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.infrastructure.security.JwtAuthenticationEntryPoint;
import com.wallet.md.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtivoController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class AtivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAtivosPorTicket useCase;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private AtivoResponse ativo() {
        return new AtivoResponse(1L, "PETR4", "PETR4", TipoAtivo.ACAO, 4,
                new BigDecimal("32.40"), new BigDecimal("1.20"));
    }

    @Test
    void deveListarAtivosComFiltros() throws Exception {
        given(useCase.listar(TipoAtivo.ACAO, "PETR")).willReturn(List.of(ativo()));

        mockMvc.perform(get("/api/v1/ativos").param("tipo", "ACAO").param("busca", "PETR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].codigo").value("PETR4"));

        verify(useCase).listar(TipoAtivo.ACAO, "PETR");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverAtivos() throws Exception {
        given(useCase.listar(null, null)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deveBuscarAtivoPorSimbolo() throws Exception {
        given(useCase.buscarPorCodigo("PETR4")).willReturn(ativo());

        mockMvc.perform(get("/api/v1/ativos/PETR4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("PETR4"));
    }

    @Test
    void deveRetornar404ParaSimboloInexistente() throws Exception {
        given(useCase.buscarPorCodigo("INVALIDO"))
                .willThrow(new ResourceNotFoundException("Ativo", "INVALIDO"));

        mockMvc.perform(get("/api/v1/ativos/INVALIDO"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveListarHistoricoDeCotacoes() throws Exception {
        given(useCase.listarCotacoes("PETR4")).willReturn(List.of(
                new CotacaoResponse(1L, new BigDecimal("32.40"), new BigDecimal("1.2"), LocalDateTime.now())
        ));

        mockMvc.perform(get("/api/v1/ativos/PETR4/cotacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].preco").value(32.40));
    }

    @Test
    void deveRetornar404ParaHistoricoDeAtivoInexistente() throws Exception {
        given(useCase.listarCotacoes("INVALIDO"))
                .willThrow(new ResourceNotFoundException("Ativo", "INVALIDO"));

        mockMvc.perform(get("/api/v1/ativos/INVALIDO/cotacoes"))
                .andExpect(status().isNotFound());
    }
}

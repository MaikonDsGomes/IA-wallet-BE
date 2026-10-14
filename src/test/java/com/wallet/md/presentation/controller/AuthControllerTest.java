package com.wallet.md.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.md.application.dto.response.AuthResponse;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.usecase.usuario.*;
import com.wallet.md.domain.exception.DuplicateResourceException;
import com.wallet.md.domain.exception.InvalidCredentialsException;
import com.wallet.md.domain.model.enums.Role;
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

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean RegisterUsuarioUseCase registerUseCase;
    @MockBean AuthenticateUsuarioUseCase authenticateUseCase;
    @MockBean AuthenticateGoogleUsuarioUseCase authenticateGoogleUseCase;
    @MockBean RefreshTokenUseCase refreshTokenUseCase;
    @MockBean LogoutUseCase logoutUseCase;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void deveRegistrarUsuario() throws Exception {
        given(registerUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .willReturn(new UsuarioResponse("Ana", "ana@teste.com", Role.USER, true, LocalDateTime.now()));
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana Teste","email":"ana@teste.com","senha":"12345678"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deveRejeitarRegistroInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"email\":\"invalido\",\"senha\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveAutenticarUsuario() throws Exception {
        given(authenticateUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .willReturn(new AuthResponse("access", "refresh", 900000L));
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@teste.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access"));
    }

    @Test
    void deveAutenticarComGoogle() throws Exception {
        given(authenticateGoogleUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .willReturn(new AuthResponse("google-access", "google-refresh", 900000L));
        mockMvc.perform(post("/api/v1/auth/google").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"credential\":\"google-credential\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("google-access"));
    }

    @Test
    void deveRejeitarCredenciaisInvalidas() throws Exception {
        given(authenticateUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .willThrow(new InvalidCredentialsException());
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@teste.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRenovarToken() throws Exception {
        given(refreshTokenUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .willReturn(new AuthResponse("novo-access", "novo-refresh", 900000L));
        mockMvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("novo-access"));
    }

    @Test
    void deveRejeitarRefreshTokenAusente() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRealizarLogout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRejeitarLogoutSemAuthorization() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }
}

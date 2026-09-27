package com.wallet.md.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.md.application.dto.response.PageResponse;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.usecase.usuario.*;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.model.enums.Role;
import com.wallet.md.infrastructure.security.JwtAuthenticationEntryPoint;
import com.wallet.md.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "1")
class UsuarioControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean GetUsuarioUseCase getUseCase;
    @MockBean ListUsuariosUseCase listUseCase;
    @MockBean UpdateUsuarioUseCase updateUseCase;
    @MockBean DeleteUsuarioUseCase deleteUseCase;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private UsuarioResponse usuario() {
        return new UsuarioResponse("Ana", "ana@teste.com", Role.USER, true, LocalDateTime.now());
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor user() {
        return SecurityMockMvcRequestPostProcessors.user("1");
    }

    @Test void deveConsultarMe() throws Exception {
        given(getUseCase.getUsuario(1L)).willReturn(usuario());
        mockMvc.perform(get("/api/v1/usuarios/me").with(user()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.email").value("ana@teste.com"));
    }

    @Test void deveFalharConsultarMeSemUsuario() throws Exception {
        given(getUseCase.getUsuario(1L)).willThrow(new ResourceNotFoundException("Usuário", 1));
        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isNotFound());
    }

    @Test void deveConsultarUsuarioPorId() throws Exception {
        given(getUseCase.getUsuario(1L)).willReturn(usuario());
        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.nome").value("Ana"));
    }

    @Test void deveRetornar404UsuarioInexistente() throws Exception {
        given(getUseCase.getUsuario(99L)).willThrow(new ResourceNotFoundException("Usuário", 99));
        mockMvc.perform(get("/api/v1/usuarios/99")).andExpect(status().isNotFound());
    }

    @Test void deveListarUsuarios() throws Exception {
        given(listUseCase.execute(0, 10)).willReturn(PageResponse.of(List.of(usuario()), 0, 10, 1));
        mockMvc.perform(get("/api/v1/usuarios")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].email").value("ana@teste.com"));
    }

    @Test void deveRejeitarPaginacaoInvalida() throws Exception {
        given(listUseCase.execute(99, 10)).willThrow(new ResourceNotFoundException("Página", 99));
        mockMvc.perform(get("/api/v1/usuarios?page=99")).andExpect(status().isNotFound());
    }

    @Test void deveAtualizarPerfil() throws Exception {
        given(updateUseCase.update(eq(1L), any())).willReturn(usuario());
        mockMvc.perform(put("/api/v1/usuarios/perfil").with(user()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana Atualizada\",\"email\":\"ana@teste.com\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
    }

    @Test void deveRejeitarPerfilInvalido() throws Exception {
        mockMvc.perform(put("/api/v1/usuarios/perfil").with(user()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"A\",\"email\":\"invalido\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test void deveAtualizarSenha() throws Exception {
        mockMvc.perform(patch("/api/v1/usuarios/perfil/senha").with(user()).param("novaSenha", "nova123"))
                .andExpect(status().isOk());
    }

    @Test void deveRejeitarSenhaAusente() throws Exception {
        mockMvc.perform(patch("/api/v1/usuarios/perfil/senha").with(user()))
                .andExpect(status().isBadRequest());
    }

    @Test void deveExcluirUsuario() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1")).andExpect(status().isOk());
    }

    @Test void deveFalharExcluirUsuarioInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Usuário", 99)).when(deleteUseCase).execute(99L);
        mockMvc.perform(delete("/api/v1/usuarios/99")).andExpect(status().isNotFound());
    }
}

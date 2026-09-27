package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.request.LoginRequest;
import com.wallet.md.application.dto.request.RefreshTokenRequest;
import com.wallet.md.application.dto.request.RegisterRequest;
import com.wallet.md.application.dto.request.UpdateUsuarioRequest;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.domain.exception.DuplicateResourceException;
import com.wallet.md.domain.exception.InvalidCredentialsException;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.model.Usuario;
import com.wallet.md.domain.model.enums.Role;
import com.wallet.md.domain.repository.usuario.TokenBlacklistRepository;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import com.wallet.md.domain.service.TokenProvider;
import com.wallet.md.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioCoreUseCasesTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock PasswordEncoderService passwordEncoder;
    @Mock UsuarioMapper usuarioMapper;
    @Mock TokenProvider tokenProvider;
    @Mock TokenBlacklistRepository blacklistRepository;
    @Mock JwtTokenProvider jwtTokenProvider;

    private Usuario usuario(boolean ativo) {
        return Usuario.builder().id(1L).nome("Ana").email("ana@teste.com")
                .senha("hash").status(ativo).role(Role.USER).criado(LocalDateTime.now()).build();
    }

    private UsuarioResponse resposta() {
        return new UsuarioResponse("Ana", "ana@teste.com", Role.USER, true, LocalDateTime.now());
    }

    @Test
    void deveRegistrarUsuario() {
        var request = new RegisterRequest("Ana", "ana@teste.com", "123456");
        given(usuarioRepository.existsByEmail(request.email())).willReturn(false);
        given(passwordEncoder.encode("123456")).willReturn("hash");
        given(usuarioRepository.save(any())).willReturn(usuario(true));
        var expected = resposta();
        given(usuarioMapper.toResponse(any())).willReturn(expected);

        assertThat(new RegisterUsuarioUseCase(usuarioRepository, passwordEncoder, usuarioMapper)
                .execute(request)).isEqualTo(expected);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveRecusarEmailDuplicado() {
        given(usuarioRepository.existsByEmail("ana@teste.com")).willReturn(true);

        assertThatThrownBy(() -> new RegisterUsuarioUseCase(usuarioRepository, passwordEncoder, usuarioMapper)
                .execute(new RegisterRequest("Ana", "ana@teste.com", "123456")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAutenticarUsuarioAtivo() {
        given(usuarioRepository.findByEmail("ana@teste.com")).willReturn(Optional.of(usuario(true)));
        given(passwordEncoder.matches("123456", "hash")).willReturn(true);
        given(tokenProvider.generateAccessToken(any())).willReturn("access");
        given(tokenProvider.generateRefreshToken(any())).willReturn("refresh");
        given(tokenProvider.getAccessTokenExpirationMs()).willReturn(900000L);

        var response = new AuthenticateUsuarioUseCase(usuarioRepository, passwordEncoder, tokenProvider)
                .execute(new LoginRequest("ana@teste.com", "123456"));

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
    }

    @Test
    void deveRecusarSenhaInvalida() {
        given(usuarioRepository.findByEmail("ana@teste.com")).willReturn(Optional.of(usuario(true)));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        assertThatThrownBy(() -> new AuthenticateUsuarioUseCase(usuarioRepository, passwordEncoder, tokenProvider)
                .execute(new LoginRequest("ana@teste.com", "errada")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void deveRenovarRefreshTokenValido() {
        given(tokenProvider.isRefreshToken("refresh")).willReturn(true);
        given(tokenProvider.validateTokenAndGetUserId("refresh")).willReturn(Optional.of(1L));
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario(true)));
        given(tokenProvider.generateAccessToken(any())).willReturn("access");
        given(tokenProvider.generateRefreshToken(any())).willReturn("novo-refresh");
        given(tokenProvider.getAccessTokenExpirationMs()).willReturn(900000L);

        assertThat(new RefreshTokenUseCase(tokenProvider, usuarioRepository)
                .execute(new RefreshTokenRequest("refresh")).accessToken()).isEqualTo("access");
    }

    @Test
    void deveRecusarRefreshTokenInvalido() {
        given(tokenProvider.isRefreshToken("invalido")).willReturn(false);

        assertThatThrownBy(() -> new RefreshTokenUseCase(tokenProvider, usuarioRepository)
                .execute(new RefreshTokenRequest("invalido")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void deveBuscarUsuario() {
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario(true)));
        var expected = resposta();
        given(usuarioMapper.toResponse(any())).willReturn(expected);

        assertThat(new GetUsuarioUseCase(usuarioRepository, usuarioMapper).getUsuario(1L))
                .isEqualTo(expected);
    }

    @Test
    void deveFalharAoBuscarUsuarioInexistente() {
        given(usuarioRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> new GetUsuarioUseCase(usuarioRepository, usuarioMapper).getUsuario(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveAtualizarEmailDisponivel() {
        var existente = usuario(true);
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(existente));
        given(usuarioRepository.existsByEmail("novo@teste.com")).willReturn(false);
        given(usuarioRepository.save(existente)).willReturn(existente);
        given(usuarioMapper.toResponse(existente)).willReturn(resposta());

        new UpdateUsuarioUseCase(usuarioRepository, usuarioMapper, passwordEncoder)
                .update(1L, new UpdateUsuarioRequest("Novo nome", "novo@teste.com"));

        assertThat(existente.getNome()).isEqualTo("Novo nome");
        assertThat(existente.getEmail()).isEqualTo("novo@teste.com");
    }

    @Test
    void deveRecusarEmailEmUsoNaAtualizacao() {
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario(true)));
        given(usuarioRepository.existsByEmail("usado@teste.com")).willReturn(true);

        assertThatThrownBy(() -> new UpdateUsuarioUseCase(usuarioRepository, usuarioMapper, passwordEncoder)
                .update(1L, new UpdateUsuarioRequest(null, "usado@teste.com")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deveAtualizarSenha() {
        var existente = usuario(true);
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(existente));
        given(passwordEncoder.encode("nova")).willReturn("novo-hash");

        new UpdateUsuarioUseCase(usuarioRepository, usuarioMapper, passwordEncoder)
                .updatePassword(1L, "nova");

        assertThat(existente.getSenha()).isEqualTo("novo-hash");
        verify(usuarioRepository).save(existente);
    }

    @Test
    void deveListarUsuariosPaginados() {
        given(usuarioRepository.findAll(0, 10)).willReturn(List.of(usuario(true)));
        given(usuarioRepository.count()).willReturn(1L);
        given(usuarioMapper.toResponseList(any())).willReturn(List.of(resposta()));

        var result = new ListUsuariosUseCase(usuarioRepository, usuarioMapper).execute(0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void deveExcluirUsuarioExistente() {
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario(true)));

        new DeleteUsuarioUseCase(usuarioRepository).execute(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deveFalharAoExcluirUsuarioInexistente() {
        given(usuarioRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> new DeleteUsuarioUseCase(usuarioRepository).execute(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveColocarTokenNaBlacklistComTtl() {
        given(jwtTokenProvider.getTimeUntilExpiration("token")).willReturn(1000L);

        new LogoutUseCase(blacklistRepository, jwtTokenProvider).execute("token");

        verify(blacklistRepository).blacklist("token", 1000L);
    }
}

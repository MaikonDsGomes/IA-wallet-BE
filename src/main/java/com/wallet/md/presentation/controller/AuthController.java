package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.request.LoginRequest;
import com.wallet.md.application.dto.request.RefreshTokenRequest;
import com.wallet.md.application.dto.request.RegisterRequest;
import com.wallet.md.application.dto.response.ApiResponse;
import com.wallet.md.application.dto.response.AuthResponse;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.usecase.usuario.AuthenticateUsuarioUseCase;
import com.wallet.md.application.usecase.usuario.LogoutUseCase;
import com.wallet.md.application.usecase.usuario.RefreshTokenUseCase;
import com.wallet.md.application.usecase.usuario.RegisterUsuarioUseCase;
import com.wallet.md.domain.exception.InvalidCredentialsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de registro, login e refresh token")
public class AuthController {

    private final RegisterUsuarioUseCase registerUseCase;
    private final AuthenticateUsuarioUseCase authenticateUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<ApiResponse<UsuarioResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var usuario = registerUseCase.execute(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuário registrado com sucesso", usuario));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter tokens JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        var auth = authenticateUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", auth));
    }

    @PostMapping("/logout")
    @Operation(summary = "Inválida tokens JWT")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
            throw new InvalidCredentialsException();
        }
        String token = authorization.substring(7);
        logoutUseCase.execute(token);
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens JWT usando refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var auth = refreshTokenUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success("Token renovado com sucesso", auth));
    }
}

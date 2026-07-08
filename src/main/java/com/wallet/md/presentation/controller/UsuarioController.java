package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.request.UpdateUsuarioRequest;
import com.wallet.md.application.dto.response.ApiResponse;
import com.wallet.md.application.dto.response.PageResponse;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.usecase.usuario.DeleteUsuarioUseCase;
import com.wallet.md.application.usecase.usuario.GetUsuarioUseCase;
import com.wallet.md.application.usecase.usuario.ListUsuariosUseCase;
import com.wallet.md.application.usecase.usuario.UpdateUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.wallet.md.presentation.utils.AuthUtils.extractUserId;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "CRUD de usuários")
public class UsuarioController {

    private final GetUsuarioUseCase getUsuarioUseCase;
    private final ListUsuariosUseCase listUsuariosUseCase;
    private final UpdateUsuarioUseCase updateUsuarioUseCase;
    private final DeleteUsuarioUseCase deleteUsuarioUseCase;

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado")
    public ResponseEntity<ApiResponse<UsuarioResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        var userId = Long.parseLong(userDetails.getUsername());
        var usuario = getUsuarioUseCase.getUsuario(userId);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID")
    public ResponseEntity<ApiResponse<UsuarioResponse>> getById(@PathVariable Long id) {
        var usuario = getUsuarioUseCase.getUsuario(id);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @GetMapping
    @Operation(summary = "Listar usuários com paginação (ADMIN)")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var usuarios = listUsuariosUseCase.execute(page, size);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @PutMapping("/perfil")
    @Operation(summary = "Atualizar dados do usuário")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUsuarioRequest request) {
        Long id = extractUserId(userDetails);
        var usuario = updateUsuarioUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Usuário atualizado com sucesso", usuario));
    }

    @PatchMapping("/perfil/senha")
    @Operation(summary = "Atualizar senha do usuário")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String novaSenha) {
        Long id = extractUserId(userDetails);
        updateUsuarioUseCase.updatePassword(id, novaSenha);
        return ResponseEntity.ok(ApiResponse.success("Senha atualizada com sucesso", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário (ADMIN)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteUsuarioUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário deletado com sucesso", null));
    }


}

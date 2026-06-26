package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.request.RegisterRequest;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.domain.exception.DuplicateResourceException;
import com.wallet.md.domain.model.Usuario;
import com.wallet.md.domain.model.enums.Role;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RegisterUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderService passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponse execute(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Usuário", "email", request.email());
        }

        var usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .role(Role.USER)
                .status(true)
                .criado(LocalDateTime.now())
                .build();

        var saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(saved);
    }
}

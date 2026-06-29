package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.request.UpdateUsuarioRequest;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.domain.exception.DuplicateResourceException;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.domain.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoderService passwordEncoder;

    public UsuarioResponse update(Long id, UpdateUsuarioRequest request) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        if (request.nome() != null) {
            usuario.setNome(request.nome());
        }

        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.email())) {
                throw new DuplicateResourceException("Usuário", "email", request.email());
            }
            usuario.setEmail(request.email());
        }

        var updated = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(updated);
    }

    public void updatePassword(Long id, String novaSenha) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}

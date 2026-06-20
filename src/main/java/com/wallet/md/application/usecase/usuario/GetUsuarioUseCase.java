package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponse getUsuario(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        return usuarioMapper.toResponse(usuario);
    }


}

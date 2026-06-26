package com.wallet.md.application.usecase.usuario;

import com.wallet.md.application.dto.response.PageResponse;
import com.wallet.md.application.dto.response.UsuarioResponse;
import com.wallet.md.application.mapper.UsuarioMapper;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public PageResponse<UsuarioResponse> execute(int page, int size) {
        var usuarios = usuarioRepository.findAll(page, size);
        var totalElements = usuarioRepository.count();
        var content = usuarioMapper.toResponseList(usuarios);
        return PageResponse.of(content, page, size, totalElements);
    }
}

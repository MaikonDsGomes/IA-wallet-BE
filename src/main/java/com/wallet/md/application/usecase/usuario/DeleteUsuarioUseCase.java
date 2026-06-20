package com.wallet.md.application.usecase.usuario;

import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public void execute(Long id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Usuário", id);
        }
        usuarioRepository.deleteById(id);
    }
}

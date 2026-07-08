package com.wallet.md.domain.repository.usuario;

import com.wallet.md.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAll(int page, int size);

    long count();

    boolean existsByEmail(String email);

    void deleteById(Long id);
}

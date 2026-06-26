package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.Usuario;
import com.wallet.md.domain.repository.usuario.UsuarioRepository;
import com.wallet.md.infrastructure.persistence.entity.UsuarioEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpaRepository;

    @Override
    public Usuario save(Usuario usuario) {
        var entity = toEntity(usuario);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<Usuario> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("criado").descending());
        return jpaRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private UsuarioEntity toEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .email(domain.getEmail())
                .senha(domain.getSenha())
                .status(domain.getStatus())
                .role(domain.getRole())
                .criado(domain.getCriado())
                .build();
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .senha(entity.getSenha())
                .status(entity.getStatus())
                .role(entity.getRole())
                .criado(entity.getCriado())
                .build();
    }
}

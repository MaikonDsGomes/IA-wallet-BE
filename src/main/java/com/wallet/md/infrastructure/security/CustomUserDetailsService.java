package com.wallet.md.infrastructure.security;

import com.wallet.md.infrastructure.persistence.entity.UsuarioEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        return buildUserDetails(usuario);
    }

    public UserDetails loadUserById(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com ID: " + id));
        return buildUserDetails(usuario);
    }

    private UserDetails buildUserDetails(UsuarioEntity usuario) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));

        return new User(
                String.valueOf(usuario.getId()),
                usuario.getSenha(),
                usuario.getStatus(),
                true, true, true,
                authorities
        );
    }
}

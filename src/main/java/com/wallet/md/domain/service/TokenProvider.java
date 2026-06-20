package com.wallet.md.domain.service;

import com.wallet.md.domain.model.Usuario;

import java.util.Optional;

public interface TokenProvider {

    String generateAccessToken(Usuario usuario);

    long getTimeUntilExpiration(String token);

    String generateRefreshToken(Usuario usuario);

    Optional<Long> validateTokenAndGetUserId(String token);

    boolean isRefreshToken(String token);

    long getAccessTokenExpirationMs();
}

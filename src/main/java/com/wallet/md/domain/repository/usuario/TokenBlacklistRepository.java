package com.wallet.md.domain.repository.usuario;

public interface TokenBlacklistRepository {
    void blacklist(String token, long ttlMs);
    boolean isBlacklisted(String token);
}

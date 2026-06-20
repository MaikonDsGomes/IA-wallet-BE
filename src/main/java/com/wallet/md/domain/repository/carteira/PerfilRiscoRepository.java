package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.model.PerfilRisco;

import java.util.Optional;

public interface PerfilRiscoRepository {
    Optional<PerfilRisco> findByUsuarioId(Long usuarioId);
}
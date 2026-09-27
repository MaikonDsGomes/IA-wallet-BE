package com.wallet.md.domain.repository.ativo;

import com.wallet.md.domain.model.Ativo;
import com.wallet.md.domain.model.enums.TipoAtivo;

import java.util.List;
import java.util.Optional;

public interface AtivoRepository {
    List<Ativo> findAtivos(TipoAtivo tipo, String busca);

    Optional<Ativo> findByCodigo(String codigo);
}

package com.wallet.md.domain.repository.carteira;

import com.wallet.md.domain.model.Movimentacao;

import java.util.List;

public interface MovimentacaoRepository {
    void registrar(Movimentacao movimentacao);

    List<Movimentacao> findByContaId(Long contaId);
}

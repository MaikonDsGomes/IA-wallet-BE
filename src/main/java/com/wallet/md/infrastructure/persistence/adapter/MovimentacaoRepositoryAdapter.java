package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.Movimentacao;
import com.wallet.md.domain.repository.carteira.MovimentacaoRepository;
import com.wallet.md.infrastructure.persistence.entity.ContaEntity;
import com.wallet.md.infrastructure.persistence.entity.MovimentacaoEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaMovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovimentacaoRepositoryAdapter implements MovimentacaoRepository {

    private final JpaMovimentacaoRepository repository;

    @Override
    public void registrar(Movimentacao movimentacao) {
        var conta = new ContaEntity();
        conta.setId(movimentacao.getContaId());
        repository.save(MovimentacaoEntity.builder()
                .conta(conta)
                .tipo(movimentacao.getTipo())
                .valor(movimentacao.getValor())
                .data(movimentacao.getData())
                .build());
    }

    @Override
    public List<Movimentacao> findByContaId(Long contaId) {
        return repository.findByContaIdOrderByDataAscIdAsc(contaId).stream()
                .map(entity -> Movimentacao.builder()
                        .id(entity.getId())
                        .contaId(entity.getConta().getId())
                        .tipo(entity.getTipo())
                        .valor(entity.getValor())
                        .data(entity.getData())
                        .build())
                .toList();
    }
}

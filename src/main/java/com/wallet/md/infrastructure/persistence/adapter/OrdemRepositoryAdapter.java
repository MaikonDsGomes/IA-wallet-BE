package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.enums.StatusOrdem;
import com.wallet.md.domain.model.enums.TipoOrdem;
import com.wallet.md.domain.model.Ordem;
import com.wallet.md.domain.repository.carteira.OrdemRepository;
import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import com.wallet.md.infrastructure.persistence.entity.ContaEntity;
import com.wallet.md.infrastructure.persistence.entity.OrdemEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaOrdemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrdemRepositoryAdapter implements OrdemRepository {

    private final JpaOrdemRepository repository;

    @Override
    public Long registrar(Long contaId, Long ativoId, TipoOrdem tipo, Integer quantidade,
                          BigDecimal precoExecucao, StatusOrdem status, LocalDateTime criadaEm) {
        var conta = new ContaEntity();
        conta.setId(contaId);
        var ativo = new AtivoEntity();
        ativo.setId(ativoId);
        return repository.save(OrdemEntity.builder()
                .conta(conta)
                .ativo(ativo)
                .tipo(tipo)
                .quantidade(quantidade)
                .precoExecucao(precoExecucao)
                .status(status)
                .criadaEm(criadaEm)
                .build()).getId();
    }

    @Override
    public List<Ordem> findByContaId(Long contaId) {
        return repository.findByContaIdOrderByCriadaEmAscIdAsc(contaId).stream()
                .map(entity -> Ordem.builder()
                        .id(entity.getId())
                        .contaId(entity.getConta().getId())
                        .ativoId(entity.getAtivo().getId())
                        .tipo(entity.getTipo())
                        .quantidade(entity.getQuantidade())
                        .precoExecucao(entity.getPrecoExecucao())
                        .status(entity.getStatus())
                        .criadaEm(entity.getCriadaEm())
                        .build())
                .toList();
    }
}

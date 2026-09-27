package com.wallet.md.infrastructure.persistence.adapter.ativo;

import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.domain.repository.ativo.CotacaoAtivoRepository;
import com.wallet.md.infrastructure.persistence.entity.CotacaoEntity;
import com.wallet.md.infrastructure.persistence.repository.ativo.JpaCotacaoAtivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CotacaoAtivoRepositoryAdapter implements CotacaoAtivoRepository {

    private final JpaCotacaoAtivoRepository jpaRepository;

    @Override
    public Optional<Cotacao> findLatestByAtivoId(Long ativoId) {
        return jpaRepository.findFirstByAtivoIdOrderByColetadoEmDesc(ativoId)
                .map(this::toDomain);
    }

    @Override
    public List<Cotacao> findHistoricoByAtivoId(Long ativoId) {
        return jpaRepository.findByAtivoIdOrderByColetadoEmDesc(ativoId).stream()
                .map(this::toDomain)
                .toList();
    }

    private Cotacao toDomain(CotacaoEntity entity) {
        return Cotacao.builder()
                .id(entity.getId())
                .ativoId(entity.getAtivo().getId())
                .preco(entity.getPreco())
                .variacao(entity.getVariacao())
                .coletadoEm(entity.getColetadoEm())
                .build();
    }
}

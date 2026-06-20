package com.wallet.md.infrastructure.persistence.adapter;

import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import com.wallet.md.infrastructure.persistence.repository.JpaCarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CarteiraRepositoryAdapter implements CarteiraRepository {

    private final JpaCarteiraRepository jpaRepository;

    @Override
    public Optional<Carteira> findByContaId(Long contaId) {
        return jpaRepository.findByContaId(contaId).map(this::toDomain);
    }

    private Carteira toDomain(CarteiraEntity entity) {
        return Carteira.builder()
                .id(entity.getId())
                .contaId(entity.getConta().getId())
                .valorTotal(entity.getValorTotal())
                .build();
    }
}

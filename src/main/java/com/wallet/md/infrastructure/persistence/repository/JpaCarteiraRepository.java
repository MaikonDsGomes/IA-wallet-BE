package com.wallet.md.infrastructure.persistence.repository;

import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface JpaCarteiraRepository extends JpaRepository<CarteiraEntity, Long> {
    Optional<CarteiraEntity> findByContaId(Long contaId);

    Optional<CarteiraEntity> findByContaUsuarioId(Long usuarioId);

    @Query("select c.conta.usuario.id from CarteiraEntity c")
    List<Long> findUsuarioIds();
}
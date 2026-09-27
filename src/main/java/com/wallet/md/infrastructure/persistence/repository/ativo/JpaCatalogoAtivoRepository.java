package com.wallet.md.infrastructure.persistence.repository.ativo;

import com.wallet.md.infrastructure.persistence.entity.AtivoEntity;
import com.wallet.md.domain.model.enums.TipoAtivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaCatalogoAtivoRepository extends JpaRepository<AtivoEntity, Long> {
    @Query("""
            SELECT a
            FROM AtivoEntity a
            WHERE a.ativo = true
              AND (:tipo IS NULL OR a.tipo = :tipo)
              AND (
                    :busca IS NULL
                    OR LOWER(a.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                    OR LOWER(a.codigo) LIKE LOWER(CONCAT('%', :busca, '%'))
              )
            ORDER BY a.codigo
            """)
    List<AtivoEntity> findAtivos(
            @Param("tipo") TipoAtivo tipo,
            @Param("busca") String busca
    );

    Optional<AtivoEntity> findByCodigoIgnoreCaseAndAtivoTrue(String codigo);
}

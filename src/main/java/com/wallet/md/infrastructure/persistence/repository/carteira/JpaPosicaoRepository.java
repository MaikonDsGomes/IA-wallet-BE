package com.wallet.md.infrastructure.persistence.repository.carteira;

import com.wallet.md.infrastructure.persistence.entity.CarteiraEntity;
import com.wallet.md.infrastructure.persistence.entity.PosicaoEntity;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaPosicaoRepository extends JpaRepository<PosicaoEntity, Long> {
    @Query("""
        SELECT
            p.id           AS id,
            p.carteiraId   AS carteiraId,
            p.ativo.id     AS ativoId,
            p.quantidade   AS quantidade,
            p.precoMedio   AS precoMedio,
            p.ativo.classe AS ativoClasse
        FROM PosicaoEntity p
        JOIN p.ativo a
        WHERE p.carteiraId = :carteiraId
    """)
    List<PosicaoComClasseProjection> findByCarteiraIdWithClasse(@Param("carteiraId") Long carteiraId);

}
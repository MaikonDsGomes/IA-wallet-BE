package com.wallet.md.infrastructure.persistence.repository.carteira;

import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.infrastructure.persistence.entity.PosicaoEntity;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaPosicaoRepository extends JpaRepository<PosicaoEntity, Long> {

    @Query("""
        SELECT
            p.ativo.id     AS ativoId,
            p.ativo.tipo   AS ativoClasse
        FROM PosicaoEntity p
        JOIN p.ativo a
        WHERE p.carteira.id = :carteiraId
    """)
    List<PosicaoComClasseProjection> findByCarteiraId(@Param("carteiraId") Long carteiraId);

    @Query("""
        SELECT
            p.ativo.id     AS ativoId,
            p.ativo.nome   AS ativoNome,
            p.ativo.codigo AS ativoCodigo,
            p.ativo.tipo   AS ativoTipo,
            p.quantidade   AS quantidade,
            p.precoMedio   AS precoMedio
        FROM PosicaoEntity p
        JOIN p.ativo a
        WHERE p.carteira.id = :carteiraId
          AND (:tipo IS NULL OR p.ativo.tipo = :tipo)
    """)
    List<PosicaoDetalhadaProjection> findDetalhadaByCarteiraId(
            @Param("carteiraId") Long carteiraId,
            @Param("tipo") TipoAtivo tipo
    );

    Optional<PosicaoEntity> findByCarteiraIdAndAtivoId(Long carteiraId, Long ativoId);

    void deleteByCarteiraIdAndAtivoId(Long carteiraId, Long ativoId);
}
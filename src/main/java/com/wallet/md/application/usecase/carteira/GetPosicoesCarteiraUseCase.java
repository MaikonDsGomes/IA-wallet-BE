package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.PosicaoResponse;
import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.CotacaoRepository;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import com.wallet.md.infrastructure.persistence.projection.PosicaoDetalhadaProjection;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class GetPosicoesCarteiraUseCase {

    private final CarteiraRepository carteiraRepository;
    private final PosicaoRepository posicaoRepository;
    private final CotacaoRepository cotacaoRepository;

    public List<PosicaoResponse> getDadosPosicao(Long usuarioId, TipoAtivo tipo) {

        var carteira = carteiraRepository.findCarteiraIdByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        List<PosicaoDetalhadaProjection> posicoes =
                posicaoRepository.findDetalhadaByCarteiraId(carteira.getId(), tipo);

        return posicoes.stream().map(p -> {
            Cotacao cotacao = cotacaoRepository.findLatestByAtivoId(p.getAtivoId());

            BigDecimal precoAtual = cotacao != null ? cotacao.getPreco() : BigDecimal.ZERO;
            BigDecimal variacaoDiaria = cotacao != null ? cotacao.getVariacao() : BigDecimal.ZERO;

            BigDecimal valorInvestido = p.getPrecoMedio()
                    .multiply(BigDecimal.valueOf(p.getQuantidade()))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal valorAtual = precoAtual
                    .multiply(BigDecimal.valueOf(p.getQuantidade()))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal variacaoPosicao = valorInvestido.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : valorAtual.subtract(valorInvestido)
                    .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            return PosicaoResponse.builder()
                    .ativoId(p.getAtivoId())
                    .ativoNome(p.getAtivoNome())
                    .ativoCodigo(p.getAtivoCodigo())
                    .ativoTipo(p.getAtivoTipo())
                    .quantidade(p.getQuantidade())
                    .precoMedio(p.getPrecoMedio())
                    .precoAtual(precoAtual)
                    .variacaoDiaria(variacaoDiaria)
                    .valorInvestido(valorInvestido)
                    .valorAtual(valorAtual)
                    .variacaoPosicao(variacaoPosicao)
                    .build();
        }).toList();
    }
}
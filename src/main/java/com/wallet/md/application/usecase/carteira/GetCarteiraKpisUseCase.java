package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.CarteiraKpisResponse;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.model.Carteira;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.CotacaoRepository;
import com.wallet.md.domain.repository.carteira.PosicaoRepository;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GetCarteiraKpisUseCase {

    private final CarteiraRepository carteiraRepository;
    private final PosicaoRepository posicaoRepository;
    private final CotacaoRepository cotacaoRepository;

    public CarteiraKpisResponse execute(Long usuarioId) {

        Carteira carteira = carteiraRepository.findCarteiraIdByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Carteira do usuário não encontrada"));

        List<PosicaoComClasseProjection> posicoes = posicaoRepository.findByCarteiraId(carteira.getId());

        BigDecimal valorTotal     = BigDecimal.ZERO;
        BigDecimal valorInvestido = BigDecimal.ZERO;
        BigDecimal valorInicioMes = BigDecimal.ZERO;
        Map<String, BigDecimal> valorPorClasse = new HashMap<>();

        LocalDate primeiroDiaMes = LocalDate.now().withDayOfMonth(1);

        for (PosicaoComClasseProjection posicao : posicoes) {

            BigDecimal precoAtual = cotacaoRepository
                    .findLatestPrecoByAtivoId(posicao.getAtivoId())
                    .orElse(posicao.getPrecoMedio());

            BigDecimal precoInicioMes = cotacaoRepository
                    .findFirstOfMonthPrecoByAtivoId(posicao.getAtivoId(), primeiroDiaMes)
                    .orElse(precoAtual);

            BigDecimal qtd = BigDecimal.valueOf(posicao.getQuantidade().longValue());

            BigDecimal valorPosicaoAtual     = precoAtual.multiply(qtd);
            BigDecimal valorPosicaoInvestido = posicao.getPrecoMedio().multiply(qtd);
            BigDecimal valorPosicaoInicioMes = precoInicioMes.multiply(qtd);

            valorTotal     = valorTotal.add(valorPosicaoAtual);
            valorInvestido = valorInvestido.add(valorPosicaoInvestido);
            valorInicioMes = valorInicioMes.add(valorPosicaoInicioMes);

            valorPorClasse.merge(posicao.getAtivoClasse(), valorPosicaoAtual, BigDecimal::add);
        }

        BigDecimal lucroAcumulado = valorTotal.subtract(valorInvestido);

        BigDecimal rentabilidadeMes = BigDecimal.ZERO;
        if (valorInicioMes.compareTo(BigDecimal.ZERO) > 0) {
            rentabilidadeMes = valorTotal
                    .divide(valorInicioMes, 10, RoundingMode.HALF_UP)
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        Map<String, BigDecimal> distribuicaoPorClasse = new HashMap<>();
        if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal finalValorTotal = valorTotal;
            valorPorClasse.forEach((classe, valor) -> {
                BigDecimal percentual = valor
                        .divide(finalValorTotal, 10, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                distribuicaoPorClasse.put(classe, percentual);
            });
        }

        return new CarteiraKpisResponse(
                valorTotal.setScale(2, RoundingMode.HALF_UP),
                valorInvestido.setScale(2, RoundingMode.HALF_UP),
                lucroAcumulado.setScale(2, RoundingMode.HALF_UP),
                rentabilidadeMes,
                distribuicaoPorClasse
        );
    }
}
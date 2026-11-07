package com.wallet.md.application.usecase.carteira;

import com.wallet.md.domain.exception.ViolacaoRegraDeNegocio;
import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.MarketPoint;
import com.wallet.md.domain.model.enums.NivelRisco;
import com.wallet.md.domain.model.enums.StatusScore;
import com.wallet.md.domain.model.enums.Volatilidade;
import com.wallet.md.domain.repository.carteira.*;
import com.wallet.md.domain.repository.mercado.AtivoSetorRepository;
import com.wallet.md.domain.repository.mercado.MarketSeriesRepository;
import com.wallet.md.infrastructure.config.ScoreMetodologiaProperties;
import com.wallet.md.infrastructure.persistence.projection.PosicaoComClasseProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CalcularScoreCarteiraUseCase {

    private final CarteiraRepository carteiraRepository;
    private final PosicaoRepository posicaoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final PerfilRiscoRepository perfilRiscoRepository;
    private final ScoreCarteiraRepository scoreCarteiraRepository;
    private final ScoreMetodologiaProperties properties;
    private final MarketSeriesRepository marketSeriesRepository;
    private final AtivoSetorRepository ativoSetorRepository;

    @Transactional
    public ScoreCarteira calcular(Long carteiraId) {
        var usuarioId = carteiraRepository.findUsuarioIdByCarteiraId(carteiraId)
                .orElseThrow(() -> new ViolacaoRegraDeNegocio("Usuário da carteira não encontrado"));
        var perfil = perfilRiscoRepository.findByUsuarioId(usuarioId).orElse(null);
        var posicoes = posicaoRepository.findByCarteiraId(carteiraId);

        if (posicoes.isEmpty()) {
            return scoreCarteiraRepository.save(ScoreCarteira.builder()
                    .carteiraId(carteiraId)
                    .status(StatusScore.PENDENTE)
                    .versaoMetodologia(properties.getMetodologiaVersao())
                    .perfilRisco(perfil == null || perfil.getNivel() == null ? null : perfil.getNivel().name())
                    .numAtivos(0)
                    .numClasses(0)
                    .numSetores(0)
                    .calculadoEm(java.time.LocalDateTime.now())
                    .build());
        }

        var valores = posicoes.stream().map(p -> valorPosicao(p)).toList();
        var valorTotal = valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (valorTotal.signum() <= 0) {
            throw new ViolacaoRegraDeNegocio("Não foi possível calcular score com valor de carteira nulo");
        }

        var pesos = valores.stream()
                .map(valor -> valor.divide(valorTotal, 10, RoundingMode.HALF_UP))
                .sorted(Comparator.reverseOrder())
                .toList();
        var concentracaoMaxima = percentual(pesos.get(0));
        var top3 = percentual(pesos.stream().limit(3).reduce(BigDecimal.ZERO, BigDecimal::add));
        var hhi = pesos.stream().map(peso -> peso.multiply(peso))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var classes = posicoes.stream().map(PosicaoComClasseProjection::getAtivoClasse).distinct().count();
        var volatilidadeMedia = volatilidadeMedia(posicoes, valores, valorTotal);
        var diversificacao = calcularDiversificacao(posicoes.size(), (int) classes, concentracaoMaxima, hhi);
        var risco = calcularRisco(posicoes, valores, valorTotal, concentracaoMaxima, perfil == null ? null : perfil.getNivel());
        var concentracaoScore = calcularConcentracao(concentracaoMaxima, hhi);
        var performance = calcularPerformance(posicoes, valores, valorTotal);
        var status = performance.valid() ? StatusScore.COMPLETO : StatusScore.PARCIAL;
        var total = mediaPonderada(diversificacao, risco, concentracaoScore,
                performance.score(), properties.getPesos());
        var setores = ativoSetorRepository.findByAtivoIds(
                        posicoes.stream().map(PosicaoComClasseProjection::getAtivoId).toList())
                .stream()
                .map(s -> s.getSetor())
                .filter(s -> s != null && !s.equalsIgnoreCase("DESCONHECIDO"))
                .collect(java.util.stream.Collectors.toSet())
                .size();

        return scoreCarteiraRepository.save(ScoreCarteira.builder()
                .carteiraId(carteiraId)
                .scoreTotal(total)
                .scoreDiversificacao(diversificacao)
                .scoreRisco(risco)
                .scoreConcentracao(concentracaoScore)
                .scorePerformance(performance.score())
                .numAtivos(posicoes.size())
                .numClasses((int) classes)
                .concentracaoMaxima(concentracaoMaxima)
                .top3Concentracao(top3)
                .indiceHerfindahl(hhi.setScale(4, RoundingMode.HALF_UP))
                .numSetores(setores)
                .volatilidade(classificarVolatilidade(volatilidadeMedia))
                .betaCarteira(performance.beta())
                .var95(performance.var95())
                .vsCdi(performance.vsCdi())
                .vsIbov(performance.vsIbov())
                .sharpeRatio(performance.sharpe())
                .status(status)
                .versaoMetodologia(properties.getMetodologiaVersao())
                .perfilRisco(perfil == null || perfil.getNivel() == null ? null : perfil.getNivel().name())
                .calculadoEm(java.time.LocalDateTime.now())
                .build());
    }

    private BigDecimal valorPosicao(PosicaoComClasseProjection posicao) {
        var cotacao = cotacaoRepository.findLatestPrecoByAtivoId(posicao.getAtivoId());
        var preco = cotacao.orElse(posicao.getPrecoMedio());
        return preco.multiply(BigDecimal.valueOf(posicao.getQuantidade()));
    }

    private int calcularDiversificacao(int ativos, int classes, BigDecimal concentracao, BigDecimal hhi) {
        var ativosScore = limitar(properties.getRegras().getPontosPorAtivo()
                .multiply(BigDecimal.valueOf(ativos)));
        var classesScore = limitar(properties.getRegras().getPontosPorClasse()
                .multiply(BigDecimal.valueOf(classes)));
        var concentracaoScore = calcularConcentracao(concentracao, hhi);
        return arredondar(media(ativosScore, classesScore, BigDecimal.valueOf(concentracaoScore)));
    }

    private int calcularConcentracao(BigDecimal concentracaoMaxima, BigDecimal hhi) {
        var maxScore = limitar(BigDecimal.valueOf(100)
                .subtract(concentracaoMaxima.multiply(properties.getRegras().getPenalidadeConcentracao())));
        var hhiScore = limitar(BigDecimal.valueOf(100)
                .multiply(BigDecimal.ONE.subtract(hhi)));
        return arredondar(maxScore.multiply(properties.getRegras().getPesoConcentracaoMaxima())
                .add(hhiScore.multiply(properties.getRegras().getPesoHhi())));
    }

    private int calcularRisco(List<PosicaoComClasseProjection> posicoes, List<BigDecimal> valores,
                              BigDecimal total, BigDecimal concentracaoMaxima, NivelRisco nivel) {
        var volatilidade = volatilidadeMedia(posicoes, valores, total);
        var score = BigDecimal.valueOf(100)
                .subtract(volatilidade.multiply(properties.getRegras().getPenalidadeVolatilidade()));
        if (nivel != null) {
            var tolerancia = properties.getPerfis().get(nivel).getVolatilidadeMaxima();
            if (volatilidade.compareTo(BigDecimal.valueOf(tolerancia)) > 0) {
                score = score.subtract(volatilidade.subtract(BigDecimal.valueOf(tolerancia))
                        .multiply(properties.getRegras().getPenalidadeDesalinhamentoPerfil()));
            }
        }
        if (concentracaoMaxima.compareTo(properties.getRegras().getLimiteConcentracaoEstrutural()) > 0) {
            score = score.subtract(concentracaoMaxima
                    .subtract(properties.getRegras().getLimiteConcentracaoEstrutural()));
        }
        return arredondar(limitar(score));
    }

    private BigDecimal volatilidadeMedia(List<PosicaoComClasseProjection> posicoes,
                                         List<BigDecimal> valores, BigDecimal total) {
        var soma = BigDecimal.ZERO;
        for (int i = 0; i < posicoes.size(); i++) {
            soma = soma.add(BigDecimal.valueOf(posicoes.get(i).getAtivoVolatilidade())
                    .multiply(valores.get(i).divide(total, 10, RoundingMode.HALF_UP)));
        }
        return soma;
    }

    private int mediaPonderada(Integer diversificacao, Integer risco, Integer concentracao,
                               Integer performance,
                               ScoreMetodologiaProperties.Pesos pesos) {
        var valor = BigDecimal.ZERO;
        var peso = BigDecimal.ZERO;
        if (diversificacao != null) {
            valor = valor.add(BigDecimal.valueOf(diversificacao).multiply(pesos.getDiversificacao()));
            peso = peso.add(pesos.getDiversificacao());
        }
        if (risco != null) {
            valor = valor.add(BigDecimal.valueOf(risco).multiply(pesos.getRisco()));
            peso = peso.add(pesos.getRisco());
        }
        if (concentracao != null) {
            valor = valor.add(BigDecimal.valueOf(concentracao).multiply(pesos.getConcentracao()));
            peso = peso.add(pesos.getConcentracao());
        }
        if (performance != null) {
            valor = valor.add(BigDecimal.valueOf(performance).multiply(pesos.getPerformance()));
            peso = peso.add(pesos.getPerformance());
        }
        return arredondar(valor.divide(peso, 4, RoundingMode.HALF_UP));
    }

    private Performance calcularPerformance(List<PosicaoComClasseProjection> posicoes,
                                             List<BigDecimal> valores, BigDecimal total) {
        var end = LocalDate.now();
        var start = end.minusDays(properties.getPerformance().getHistoricoDias());
        var assetReturns = new ArrayList<Map<LocalDate, BigDecimal>>();
        for (var posicao : posicoes) {
            List<MarketPoint> points = marketSeriesRepository.findByInstrumentoAndTipo(
                    posicao.getAtivoCodigo(), "ATIVO", start, end);
            if (points.size() < properties.getPerformance().getMinObservacoes()) {
                return Performance.unavailable();
            }
            var prices = points.stream()
                    .filter(p -> p.getValorAjustado() != null || p.getValor() != null)
                    .collect(java.util.stream.Collectors.toMap(
                            p -> p.getData(),
                            p -> p.getValorAjustado() != null ? p.getValorAjustado() : p.getValor(),
                            (first, ignored) -> first));
            assetReturns.add(dailyReturns(prices));
        }
        var commonDates = new HashSet<>(assetReturns.get(0).keySet());
        assetReturns.forEach(map -> commonDates.retainAll(map.keySet()));
        var benchmark = series("IBOVESPA", properties.getPerformance().getBenchmarkInstrument(), start, end);
        var cdi = series("CDI", properties.getPerformance().getCdiInstrument(), start, end);
        if (commonDates.size() < properties.getPerformance().getMinObservacoes()
                || benchmark.size() < properties.getPerformance().getMinObservacoes()
                || cdi.size() < properties.getPerformance().getMinObservacoes()) {
            return Performance.unavailable();
        }

        var portfolio = new HashMap<LocalDate, BigDecimal>();
        var weights = valores.stream()
                .map(value -> value.divide(total, 10, RoundingMode.HALF_UP)).toList();
        for (var date : commonDates) {
            var daily = BigDecimal.ZERO;
            for (var i = 0; i < assetReturns.size(); i++) {
                daily = daily.add(assetReturns.get(i).get(date).multiply(weights.get(i)));
            }
            portfolio.put(date, daily);
        }
        var benchmarkReturns = dailyReturns(benchmark);
        var cdiReturns = new HashMap<LocalDate, BigDecimal>();
        cdi.forEach((date, value) -> cdiReturns.put(date,
                value.divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)));
        var dates = new HashSet<>(portfolio.keySet());
        dates.retainAll(benchmarkReturns.keySet());
        dates.retainAll(cdiReturns.keySet());
        if (dates.size() < properties.getPerformance().getMinObservacoes()) return Performance.unavailable();

        var portfolioList = dates.stream().sorted().map(portfolio::get).toList();
        var benchmarkList = dates.stream().sorted().map(benchmarkReturns::get).toList();
        var cdiList = dates.stream().sorted().map(cdiReturns::get).toList();
        var portfolioReturn = cumulative(portfolioList);
        var benchmarkReturn = cumulative(benchmarkList);
        var cdiReturn = cumulative(cdiList);
        var mean = average(portfolioList);
        var std = standardDeviation(portfolioList, mean);
        var covariance = covariance(portfolioList, benchmarkList);
        var benchmarkVariance = variance(benchmarkList, average(benchmarkList));
        BigDecimal beta = benchmarkVariance.signum() == 0 ? null :
                covariance.divide(benchmarkVariance, 8, RoundingMode.HALF_UP);
        var sharpe = std.signum() == 0 ? null : mean.divide(std, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(Math.sqrt(252)));
        var var95 = percentile(portfolioList, 0.05).negate();
        var score = limitar(BigDecimal.valueOf(50)
                .add(portfolioReturn.multiply(BigDecimal.valueOf(1000))));
        return new Performance(true, arredondar(score), beta, var95, portfolioReturn.subtract(cdiReturn)
                .multiply(BigDecimal.valueOf(100)), portfolioReturn.subtract(benchmarkReturn)
                .multiply(BigDecimal.valueOf(100)), sharpe);
    }

    private Map<LocalDate, BigDecimal> series(String tipo, String instrumento, LocalDate start, LocalDate end) {
        return marketSeriesRepository.findByInstrumentoAndTipo(instrumento, tipo, start, end).stream()
                .filter(p -> p.getValorAjustado() != null || p.getValor() != null)
                .collect(java.util.stream.Collectors.toMap(
                        p -> p.getData(),
                        p -> p.getValorAjustado() != null ? p.getValorAjustado() : p.getValor(),
                        (first, ignored) -> first));
    }

    private Map<LocalDate, BigDecimal> dailyReturns(Map<LocalDate, BigDecimal> prices) {
        var dates = prices.keySet().stream().sorted().toList();
        var result = new HashMap<LocalDate, BigDecimal>();
        for (var i = 1; i < dates.size(); i++) {
            var previous = prices.get(dates.get(i - 1));
            var current = prices.get(dates.get(i));
            if (previous.signum() != 0) {
                result.put(dates.get(i), current.divide(previous, 12, RoundingMode.HALF_UP).subtract(BigDecimal.ONE));
            }
        }
        return result;
    }

    private BigDecimal cumulative(List<BigDecimal> returns) {
        var value = BigDecimal.ONE;
        for (var item : returns) value = value.multiply(BigDecimal.ONE.add(item));
        return value.subtract(BigDecimal.ONE);
    }

    private BigDecimal average(List<BigDecimal> values) {
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), 12, RoundingMode.HALF_UP);
    }

    private BigDecimal variance(List<BigDecimal> values, BigDecimal mean) {
        return values.stream().map(value -> value.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, values.size() - 1)), 12, RoundingMode.HALF_UP);
    }

    private BigDecimal standardDeviation(List<BigDecimal> values, BigDecimal mean) {
        return BigDecimal.valueOf(Math.sqrt(variance(values, mean).doubleValue()));
    }

    private BigDecimal covariance(List<BigDecimal> first, List<BigDecimal> second) {
        var meanFirst = average(first);
        var meanSecond = average(second);
        var sum = BigDecimal.ZERO;
        for (var i = 0; i < first.size(); i++) {
            sum = sum.add(first.get(i).subtract(meanFirst).multiply(second.get(i).subtract(meanSecond)));
        }
        return sum.divide(BigDecimal.valueOf(Math.max(1, first.size() - 1)), 12, RoundingMode.HALF_UP);
    }

    private BigDecimal percentile(List<BigDecimal> values, double percentile) {
        var sorted = values.stream().sorted().toList();
        var index = Math.max(0, Math.min(sorted.size() - 1, (int) Math.floor(percentile * sorted.size())));
        return sorted.get(index);
    }

    private BigDecimal media(BigDecimal... valores) {
        var soma = java.util.Arrays.stream(valores).reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(valores.length), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal percentual(BigDecimal peso) {
        return peso.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal limitar(BigDecimal valor) {
        return valor.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
    }

    private int arredondar(BigDecimal valor) {
        return limitar(valor).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private Volatilidade classificarVolatilidade(BigDecimal media) {
        if (media.compareTo(BigDecimal.valueOf(properties.getRegras().getVolatilidadeBaixaMax())) <= 0) {
            return Volatilidade.Baixa;
        }
        if (media.compareTo(BigDecimal.valueOf(properties.getRegras().getVolatilidadeMediaMax())) <= 0) {
            return Volatilidade.Media;
        }
        return Volatilidade.Alta;
    }

    private record Performance(boolean valid, Integer score, BigDecimal beta, BigDecimal var95,
                               BigDecimal vsCdi, BigDecimal vsIbov, BigDecimal sharpe) {
        private static Performance unavailable() {
            return new Performance(false, null, null, null, null, null, null);
        }
    }
}

package com.wallet.md.infrastructure.config;

import com.wallet.md.domain.model.enums.NivelRisco;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "investwallet.score")
public class ScoreMetodologiaProperties {

    private String metodologiaVersao = "v1";
    private int debounceMinutos = 10;
    private int intervaloSchedulerSegundos = 60;
    private int staleProcessingMinutos = 15;
    private int maxTentativas = 5;
    private Classificacao classificacao = new Classificacao();
    private Pesos pesos = new Pesos();
    private Regras regras = new Regras();
    private Performance performance = new Performance();
    private Map<NivelRisco, Perfil> perfis = new EnumMap<>(NivelRisco.class);

    @PostConstruct
    void validar() {
        var soma = pesos.diversificacao
                .add(pesos.risco)
                .add(pesos.concentracao)
                .add(pesos.performance);
        if (soma.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalStateException("Os pesos do score devem somar 1.0");
        }
        perfis.putIfAbsent(NivelRisco.CONSERVADOR, new Perfil(3));
        perfis.putIfAbsent(NivelRisco.MODERADO, new Perfil(6));
        perfis.putIfAbsent(NivelRisco.AGRESSIVO, new Perfil(9));
    }

    @Getter
    @Setter
    public static class Pesos {
        private BigDecimal diversificacao = new BigDecimal("0.25");
        private BigDecimal risco = new BigDecimal("0.30");
        private BigDecimal concentracao = new BigDecimal("0.25");
        private BigDecimal performance = new BigDecimal("0.20");
    }

    @Getter
    @Setter
    public static class Regras {
        private BigDecimal pontosPorAtivo = new BigDecimal("20");
        private BigDecimal pontosPorClasse = new BigDecimal("33.3333");
        private BigDecimal penalidadeConcentracao = new BigDecimal("1.5");
        private BigDecimal penalidadeVolatilidade = new BigDecimal("10");
        private BigDecimal penalidadeDesalinhamentoPerfil = new BigDecimal("10");
        private BigDecimal limiteConcentracaoEstrutural = new BigDecimal("40");
        private BigDecimal pesoConcentracaoMaxima = new BigDecimal("0.60");
        private BigDecimal pesoHhi = new BigDecimal("0.40");
        private int volatilidadeBaixaMax = 3;
        private int volatilidadeMediaMax = 6;
    }

    @Getter
    @Setter
    public static class Classificacao {
        private int criticoMax = 39;
        private int atencaoMax = 59;
        private int bomMax = 79;
        private int muitoBomMax = 89;
    }

    @Getter
    @Setter
    public static class Perfil {
        private int volatilidadeMaxima;

        public Perfil() {
        }

        public Perfil(int volatilidadeMaxima) {
            this.volatilidadeMaxima = volatilidadeMaxima;
        }

    }

    @Getter
    @Setter
    public static class Performance {
        private int historicoDias = 365;
        private int minObservacoes = 60;
        private String benchmarkInstrument = "^BVSP";
        private String cdiInstrument = "cdi";
    }
}

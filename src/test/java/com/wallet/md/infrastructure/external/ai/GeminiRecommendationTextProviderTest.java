package com.wallet.md.infrastructure.external.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.md.domain.model.Recomendacao;
import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.Prioridade;
import com.wallet.md.domain.model.enums.Volatilidade;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiRecommendationTextProviderTest {

    @Test
    void deveEnviarConfiguracaoDeJsonEInterpretarResposta() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var provider = new GeminiRecommendationTextProvider(
                builder, new ObjectMapper(), "secret", "gemini-test", true);

        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemini-test:generateContent?key=secret"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().json("""
                        {"generationConfig":{"responseMimeType":"application/json"}}
                        """, false))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"{\\"titulo\\":\\"Texto IA\\",\\"descricao\\":\\"Explicação segura\\",\\"categoria\\":\\"RISCO\\",\\"prioridade\\":\\"ALTA\\"}"}]}}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = provider.improve(recomendacao(), score(), null);

        assertThat(result.getTitulo()).isEqualTo("Texto IA");
        assertThat(result.getCategoria()).isEqualTo(CategoriaRecomendacao.RISCO);
        server.verify();
    }

    @Test
    void deveRejeitarIndicacaoDiretaDeNegociacao() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var provider = new GeminiRecommendationTextProvider(
                builder, new ObjectMapper(), "secret", "gemini-test", true);
        server.expect(requestTo(org.hamcrest.Matchers.containsString("gemini-test:generateContent")))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"{\\"titulo\\":\\"Compre PETR4\\",\\"descricao\\":\\"Compre agora\\",\\"categoria\\":\\"OPORTUNIDADE\\",\\"prioridade\\":\\"ALTA\\"}"}]}}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> provider.improve(recomendacao(), score(), null))
                .isInstanceOf(AiProviderException.class)
                .hasMessageContaining("negociação");
        server.verify();
    }

    private Recomendacao recomendacao() {
        return Recomendacao.builder()
                .carteiraId(10L)
                .titulo("Revise o risco")
                .descricao("Texto base")
                .categoria(CategoriaRecomendacao.RISCO)
                .prioridade(Prioridade.ALTA)
                .impactoPts(-10)
                .ativa(true)
                .build();
    }

    private ScoreCarteira score() {
        return ScoreCarteira.builder()
                .scoreTotal(55)
                .scoreRisco(45)
                .scoreDiversificacao(60)
                .scoreConcentracao(50)
                .volatilidade(Volatilidade.Alta)
                .concentracaoMaxima(new BigDecimal("45"))
                .build();
    }
}

package com.wallet.md.infrastructure.external.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.md.application.service.RecommendationTextProvider;
import com.wallet.md.domain.model.PerfilRisco;
import com.wallet.md.domain.model.Recomendacao;
import com.wallet.md.domain.model.ScoreCarteira;
import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.Prioridade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GeminiRecommendationTextProvider implements RecommendationTextProvider {

    private static final String RESPONSE_SCHEMA = """
            {
              "type": "OBJECT",
              "properties": {
                "titulo": {"type": "STRING"},
                "descricao": {"type": "STRING"},
                "categoria": {"type": "STRING", "enum": ["RISCO", "OPORTUNIDADE", "POSITIVO"]},
                "prioridade": {"type": "STRING", "enum": ["ALTA", "MEDIA", "BAIXA"]}
              },
              "required": ["titulo", "descricao", "categoria", "prioridade"]
            }
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final boolean enabled;

    public GeminiRecommendationTextProvider(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${investwallet.ia.gemini.api-key:}") String apiKey,
            @Value("${investwallet.ia.gemini.model:gemini-2.0-flash}") String model,
            @Value("${investwallet.ia.gemini.enabled:false}") boolean enabled) {
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.enabled = enabled;
    }

    @Override
    public Recomendacao improve(Recomendacao recommendation, ScoreCarteira score, PerfilRisco profile) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            return recommendation;
        }

        try {
            var response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "contents", new Object[]{Map.of(
                                    "parts", new Object[]{Map.of("text", prompt(recommendation, score, profile))}
                            )},
                            "generationConfig", Map.of(
                                    "responseMimeType", "application/json",
                                    "responseSchema", objectMapper.readTree(RESPONSE_SCHEMA)
                            )
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            return parse(response, recommendation);
        } catch (AiProviderException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AiProviderException("Falha ao gerar texto de recomendação no Gemini", exception);
        }
    }

    private Recomendacao parse(JsonNode response, Recomendacao original) {
        var text = response == null
                ? null
                : response.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText(null);
        if (text == null || text.isBlank()) {
            throw new AiProviderException("Gemini retornou resposta sem texto estruturado");
        }

        try {
            var json = objectMapper.readTree(text);
            var titulo = requiredText(json, "titulo", 150);
            var descricao = requiredText(json, "descricao", 500);
            var categoria = CategoriaRecomendacao.valueOf(requiredText(json, "categoria", 30));
            var prioridade = Prioridade.valueOf(requiredText(json, "prioridade", 30));

            if (containsTradingInstruction(titulo) || containsTradingInstruction(descricao)) {
                throw new AiProviderException("Resposta do Gemini contém indicação direta de negociação");
            }

            return Recomendacao.builder()
                    .id(original.getId())
                    .carteiraId(original.getCarteiraId())
                    .ativoId(original.getAtivoId())
                    .titulo(titulo)
                    .descricao(descricao)
                    .categoria(categoria)
                    .prioridade(prioridade)
                    .impactoPts(original.getImpactoPts())
                    .ativa(original.getAtiva())
                    .geradaEm(original.getGeradaEm())
                    .build();
        } catch (AiProviderException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AiProviderException("Gemini retornou JSON fora do contrato", exception);
        }
    }

    private String requiredText(JsonNode json, String field, int maxLength) {
        var value = json.path(field).asText(null);
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new AiProviderException("Campo inválido na resposta do Gemini: " + field);
        }
        return value.trim();
    }

    private String prompt(Recomendacao recommendation, ScoreCarteira score, PerfilRisco profile) {
        return """
                Você é um assistente educacional de análise de carteiras.
                Reescreva a recomendação abaixo em português do Brasil, de forma clara e objetiva.
                Retorne somente JSON válido no schema solicitado. Não use markdown.
                Não indique compra, venda, preço, ticker ou ordem específica.
                Não invente dados e preserve a categoria e a prioridade recebidas.

                Recomendação base:
                titulo=%s
                descricao=%s
                categoria=%s
                prioridade=%s
                perfil=%s
                scoreTotal=%s
                scoreRisco=%s
                scoreDiversificacao=%s
                scoreConcentracao=%s
                volatilidade=%s
                """.formatted(
                recommendation.getTitulo(), recommendation.getDescricao(),
                recommendation.getCategoria(), recommendation.getPrioridade(),
                profile == null ? "NAO_INFORMADO" : profile.getNivel(),
                score.getScoreTotal(), score.getScoreRisco(), score.getScoreDiversificacao(),
                score.getScoreConcentracao(), score.getVolatilidade());
    }

    private boolean containsTradingInstruction(String value) {
        var normalized = value.toLowerCase();
        return normalized.contains("compre ") || normalized.contains("compre.")
                || normalized.contains("venda ") || normalized.contains("venda.")
                || normalized.contains("comprar ") || normalized.contains("vender ");
    }
}

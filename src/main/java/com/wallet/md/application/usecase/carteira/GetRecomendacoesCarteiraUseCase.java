package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.RecomendacaoResponse;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.RecomendacaoRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.Prioridade;

@RequiredArgsConstructor
public class GetRecomendacoesCarteiraUseCase {

    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final RecomendacaoRepository recomendacaoRepository;
    private final com.wallet.md.domain.repository.carteira.AtivoRepository ativoRepository;

    public GetRecomendacoesCarteiraUseCase(ContaRepository c, CarteiraRepository k, RecomendacaoRepository r) {
        this(c, k, r, null);
    }

    public List<RecomendacaoResponse> getRecomendacoes(Long usuarioId) {
        return getRecomendacoes(usuarioId, null, null, 0, Integer.MAX_VALUE);
    }
    public List<RecomendacaoResponse> getRecomendacoes(Long usuarioId, CategoriaRecomendacao categoria,
                                                        Prioridade prioridade, int pagina, int tamanho) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta", usuarioId));
        var carteira = carteiraRepository.findByContaId(conta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira", conta.getId()));

        var result = recomendacaoRepository.findAtivasByCarteiraId(carteira.getId()).stream()
                .filter(r -> categoria == null || r.getCategoria() == categoria)
                .filter(r -> prioridade == null || r.getPrioridade() == prioridade)
                .map(recomendacao -> new RecomendacaoResponse(
                        recomendacao.getId(),
                        recomendacao.getAtivoId(),
                        recomendacao.getTitulo(),
                        recomendacao.getDescricao(),
                        recomendacao.getCategoria(),
                        recomendacao.getPrioridade(),
                        recomendacao.getImpactoPts(),
                        recomendacao.getGeradaEm(),
                        null,
                        null,
                        null,
                        tipo(recomendacao.getCategoria()),
                        cor(recomendacao.getCategoria()),
                        icone(recomendacao.getCategoria()),
                        acaoSugerida(recomendacao.getTitulo())
                )).map(response -> {
                    if (ativoRepository == null || response.ativoId() == null) return response;
                    return ativoRepository.findById(response.ativoId()).map(a -> new RecomendacaoResponse(
                            response.id(), response.ativoId(), response.titulo(), response.descricao(),
                            response.categoria(), response.prioridade(), response.impactoPts(), response.geradaEm(),
                            a.getCodigo(), a.getNome(), a.getTipo() == null ? null : a.getTipo().name(),
                            response.tipo(), response.cor(), response.icone(), response.acaoSugerida())).orElse(response);
                }).toList();
        int from = Math.max(0, pagina) * Math.max(1, tamanho);
        return from >= result.size() ? List.of() : result.subList(from, Math.min(result.size(), from + Math.max(1, tamanho)));
    }

    private String tipo(CategoriaRecomendacao categoria) {
        return switch (categoria) {
            case RISCO -> "RISCO";
            case OPORTUNIDADE -> "OPORTUNIDADE";
            case POSITIVO -> "POSITIVO";
        };
    }

    private String cor(CategoriaRecomendacao categoria) {
        return switch (categoria) {
            case RISCO -> "VERMELHO";
            case OPORTUNIDADE -> "AMARELO";
            case POSITIVO -> "VERDE";
        };
    }

    private String icone(CategoriaRecomendacao categoria) {
        return switch (categoria) {
            case RISCO -> "alert-triangle";
            case OPORTUNIDADE -> "lightbulb";
            case POSITIVO -> "check-circle";
        };
    }

    private String acaoSugerida(String titulo) {
        if (titulo.startsWith("Revise")) return "REVISAR";
        if (titulo.startsWith("Reduza")) return "REDUZIR";
        if (titulo.startsWith("Aumente")) return "AUMENTAR";
        if (titulo.startsWith("Alinhe")) return "ALINHAR";
        return "ACOMPANHAR";
    }
}

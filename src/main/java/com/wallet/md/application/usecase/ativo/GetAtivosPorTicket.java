package com.wallet.md.application.usecase.ativo;

import com.wallet.md.application.dto.response.ativo.AtivoResponse;
import com.wallet.md.application.dto.response.ativo.CotacaoResponse;
import com.wallet.md.domain.exception.ResourceNotFoundException;
import com.wallet.md.domain.model.Ativo;
import com.wallet.md.domain.model.Cotacao;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.domain.repository.ativo.AtivoRepository;
import com.wallet.md.domain.repository.ativo.CotacaoAtivoRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetAtivosPorTicket {

    private final AtivoRepository ativoRepository;
    private final CotacaoAtivoRepository cotacaoRepository;

    public List<AtivoResponse> listar(TipoAtivo tipo, String busca) {
        var termo = busca == null || busca.isBlank() ? null : busca.trim();
        return ativoRepository.findAtivos(tipo, termo).stream()
                .map(ativo -> toResponse(ativo, cotacaoRepository.findLatestByAtivoId(ativo.getId()).orElse(null)))
                .toList();
    }

    public AtivoResponse buscarPorCodigo(String codigo) {
        var ativo = ativoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Ativo", codigo));
        var cotacao = cotacaoRepository.findLatestByAtivoId(ativo.getId()).orElse(null);
        return toResponse(ativo, cotacao);
    }

    public List<CotacaoResponse> listarCotacoes(String codigo) {
        var ativo = ativoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Ativo", codigo));
        return cotacaoRepository.findHistoricoByAtivoId(ativo.getId()).stream()
                .map(this::toCotacaoResponse)
                .toList();
    }

    private AtivoResponse toResponse(Ativo ativo, Cotacao cotacao) {
        return new AtivoResponse(
                ativo.getId(),
                ativo.getNome(),
                ativo.getCodigo(),
                ativo.getTipo(),
                ativo.getVolatilidade(),
                cotacao != null ? cotacao.getPreco() : null,
                cotacao != null ? cotacao.getVariacao() : null
        );
    }

    private CotacaoResponse toCotacaoResponse(Cotacao cotacao) {
        return new CotacaoResponse(
                cotacao.getId(),
                cotacao.getPreco(),
                cotacao.getVariacao(),
                cotacao.getColetadoEm()
        );
    }
}

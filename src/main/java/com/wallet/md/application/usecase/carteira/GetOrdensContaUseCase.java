package com.wallet.md.application.usecase.carteira;

import com.wallet.md.application.dto.response.carteira.OrdemResponse;
import com.wallet.md.domain.exception.NotFoundException;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.OrdemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetOrdensContaUseCase {

    private final ContaRepository contaRepository;
    private final OrdemRepository ordemRepository;

    public List<OrdemResponse> listar(Long usuarioId) {
        var conta = contaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        return ordemRepository.findByContaId(conta.getId()).stream()
                .map(ordem -> new OrdemResponse(
                        ordem.getId(), ordem.getAtivoId(), ordem.getTipo(), ordem.getQuantidade(),
                        ordem.getPrecoExecucao(), ordem.getStatus(), ordem.getCriadaEm()))
                .toList();
    }
}

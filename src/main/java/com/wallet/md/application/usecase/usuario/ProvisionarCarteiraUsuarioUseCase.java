package com.wallet.md.application.usecase.usuario;

import com.wallet.md.domain.repository.carteira.CarteiraRepository;
import com.wallet.md.domain.repository.carteira.ContaRepository;
import com.wallet.md.domain.repository.carteira.PerfilRiscoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ProvisionarCarteiraUsuarioUseCase {

    private final ContaRepository contaRepository;
    private final CarteiraRepository carteiraRepository;
    private final PerfilRiscoRepository perfilRiscoRepository;

    @Transactional
    public void execute(Long usuarioId) {
        var conta = contaRepository.createIfAbsent(usuarioId);
        carteiraRepository.createIfAbsent(conta.getId());
        perfilRiscoRepository.createIfAbsent(usuarioId);
    }
}

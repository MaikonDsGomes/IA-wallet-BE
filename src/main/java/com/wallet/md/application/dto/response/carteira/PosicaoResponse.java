package com.wallet.md.application.dto.response.carteira;

import com.wallet.md.domain.model.enums.TipoAtivo;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosicaoResponse {
    private Long ativoId;
    private String ativoNome;
    private String ativoCodigo;
    private TipoAtivo ativoTipo;
    private Integer quantidade;
    private BigDecimal precoMedio;
    private BigDecimal precoAtual;
    private BigDecimal variacaoDiaria;
    private BigDecimal valorInvestido;
    private BigDecimal valorAtual;
    private BigDecimal variacaoPosicao;
}
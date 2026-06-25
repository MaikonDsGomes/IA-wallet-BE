package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.TipoAtivo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ativo {

    private Long id;
    private String nome;
    private String codigo;
    private TipoAtivo tipo;
    private Integer volatilidade;
    private Boolean ativo;
}

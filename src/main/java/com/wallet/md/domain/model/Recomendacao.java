package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.CategoriaRecomendacao;
import com.wallet.md.domain.model.enums.Prioridade;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recomendacao {

    private Long id;
    private Long carteiraId;
    private Long ativoId;
    private String titulo;
    private String descricao;
    private CategoriaRecomendacao categoria;
    private Prioridade prioridade;
    private Integer impactoPts;
    private Boolean ativa;
    private LocalDateTime geradaEm;
}

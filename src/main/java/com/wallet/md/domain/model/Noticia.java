package com.wallet.md.domain.model;

import com.wallet.md.domain.model.enums.Sentimento;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Noticia {

    private Long id;
    private String titulo;
    private String descricao;
    private String fonte;
    private String url;
    private String urlHash;
    private Sentimento sentimento;
    private LocalDateTime publicadaEm;
    private LocalDateTime criadaEm;
}

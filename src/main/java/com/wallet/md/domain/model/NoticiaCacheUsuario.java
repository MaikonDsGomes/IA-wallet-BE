package com.wallet.md.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticiaCacheUsuario {

    private Long id;
    private Long usuarioId;
    private Long noticiaId;
    private Boolean lida;
    private LocalDateTime exibidaEm;
}

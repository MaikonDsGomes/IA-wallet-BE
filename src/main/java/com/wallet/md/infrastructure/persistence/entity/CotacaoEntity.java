package com.wallet.md.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotacao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotacaoEntity {
    private Long id;
    private Long ativoId;
    private BigDecimal preco;
    private BigDecimal variacao;
    private LocalDateTime coletadoEm;
}

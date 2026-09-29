package com.wallet.md.application.dto.response.carteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EvolucaoCarteiraResponse(LocalDateTime data, BigDecimal saldo, BigDecimal entradas,
                                       BigDecimal saidas, BigDecimal variacao) {}

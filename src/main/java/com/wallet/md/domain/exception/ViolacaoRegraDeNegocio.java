package com.wallet.md.domain.exception;

public class ViolacaoRegraDeNegocio extends RuntimeException {
    public ViolacaoRegraDeNegocio(String message) {
        super(message);
    }
}

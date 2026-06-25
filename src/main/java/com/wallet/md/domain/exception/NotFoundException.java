package com.wallet.md.domain.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String msg) {
        super(String.format("não encontrado com identificador: %s", msg));
    }

}

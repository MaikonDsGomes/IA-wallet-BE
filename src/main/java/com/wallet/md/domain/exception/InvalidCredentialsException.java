package com.wallet.md.domain.exception;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }
}

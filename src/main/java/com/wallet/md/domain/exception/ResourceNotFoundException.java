package com.wallet.md.domain.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super(String.format("%s não encontrado com identificador: %s", resource, identifier));
    }
}

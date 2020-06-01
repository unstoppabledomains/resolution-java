package com.unstoppabledomains.exceptions;

public class ContractCallException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ContractCallException(String message) {
        super(message);
    }

    public ContractCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
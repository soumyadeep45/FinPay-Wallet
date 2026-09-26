package com.finpay.wallet_service.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message); // Passes the message up to the parent RuntimeException class
    }
}
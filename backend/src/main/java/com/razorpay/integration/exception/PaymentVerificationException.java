package com.razorpay.integration.exception;

public class PaymentVerificationException extends RuntimeException {

    public PaymentVerificationException(String message) {
        super(message);
    }
}

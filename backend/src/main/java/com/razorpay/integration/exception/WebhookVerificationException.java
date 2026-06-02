package com.razorpay.integration.exception;

public class WebhookVerificationException extends RuntimeException {

    public WebhookVerificationException(String message) {
        super(message);
    }
}

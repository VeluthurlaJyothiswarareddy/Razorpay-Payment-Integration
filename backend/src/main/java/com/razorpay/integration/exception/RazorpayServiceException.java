package com.razorpay.integration.exception;

public class RazorpayServiceException extends RuntimeException {

    public RazorpayServiceException(String message) {
        super(message);
    }

    public RazorpayServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

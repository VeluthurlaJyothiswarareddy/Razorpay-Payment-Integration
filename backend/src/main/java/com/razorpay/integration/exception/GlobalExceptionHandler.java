package com.razorpay.integration.exception;

import com.razorpay.integration.dto.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(PaymentVerificationException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentVerification(PaymentVerificationException ex) {
        log.warn("Payment verification failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Payment Verification Failed", ex.getMessage());
    }

    @ExceptionHandler(WebhookVerificationException.class)
    public ResponseEntity<ApiErrorResponse> handleWebhookVerification(WebhookVerificationException ex) {
        log.warn("Webhook verification failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Webhook Verification Failed", ex.getMessage());
    }

    @ExceptionHandler(RazorpayServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleRazorpayService(RazorpayServiceException ex) {
        log.error("Razorpay service error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_GATEWAY, "Razorpay Error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String error, String message) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}

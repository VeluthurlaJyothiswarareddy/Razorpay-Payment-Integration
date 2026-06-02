package com.razorpay.integration.dto.response;

import com.razorpay.integration.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentResponse {

    private boolean verified;
    private String paymentId;
    private String orderId;
    private Long amount;
    private String currency;
    private PaymentStatus status;
    private String message;
    private Instant verifiedAt;
}

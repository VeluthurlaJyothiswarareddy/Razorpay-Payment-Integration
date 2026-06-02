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
public class PaymentResponse {

    private String id;
    private String paymentId;
    private String orderId;
    private Long amount;
    private String currency;
    private PaymentStatus status;
    private String method;
    private String email;
    private String contact;
    private Instant createdAt;
    private Instant updatedAt;
}

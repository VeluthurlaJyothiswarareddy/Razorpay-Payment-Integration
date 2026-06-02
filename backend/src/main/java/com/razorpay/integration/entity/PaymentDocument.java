package com.razorpay.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class PaymentDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String paymentId;

    @Indexed
    private String orderId;

    private Long amount;

    private String currency;

    private PaymentStatus status;

    private String signature;

    private String method;

    private String email;

    private String contact;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

package com.razorpay.integration.dto.response;

import com.razorpay.integration.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {

    private String orderId;
    private Long amount;
    private String currency;
    private OrderStatus status;
    private String keyId;
    private String receipt;
    private Instant createdAt;
}

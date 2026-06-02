package com.razorpay.integration.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 100, message = "Amount must be at least 100 paise (₹1)")
    private Long amount;

    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "INR";

    private String receipt;

    private String notes;
}

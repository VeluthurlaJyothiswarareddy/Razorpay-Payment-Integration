package com.razorpay.integration.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequest {

    @NotBlank(message = "Razorpay order ID is required")
    @JsonProperty("razorpayOrderId")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    @JsonProperty("razorpayPaymentId")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    @JsonProperty("razorpaySignature")
    private String razorpaySignature;
}

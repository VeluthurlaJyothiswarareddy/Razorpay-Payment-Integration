package com.razorpay.integration.controller;

import com.razorpay.integration.dto.request.CreateOrderRequest;
import com.razorpay.integration.dto.request.VerifyPaymentRequest;
import com.razorpay.integration.dto.response.CreateOrderResponse;
import com.razorpay.integration.dto.response.OrderResponse;
import com.razorpay.integration.dto.response.PaymentResponse;
import com.razorpay.integration.dto.response.VerifyPaymentResponse;
import com.razorpay.integration.dto.response.WebhookResponse;
import com.razorpay.integration.service.PaymentVerificationService;
import com.razorpay.integration.service.RazorpayOrderService;
import com.razorpay.integration.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Razorpay payment integration APIs")
public class PaymentController {

    private final RazorpayOrderService razorpayOrderService;
    private final PaymentVerificationService paymentVerificationService;
    private final WebhookService webhookService;

    @PostMapping("/payments/create-order")
    @Operation(summary = "Create a Razorpay order", description = "Creates an order via Razorpay SDK and persists it in MongoDB")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(razorpayOrderService.createOrder(request));
    }

    @PostMapping("/payments/verify")
    @Operation(summary = "Verify payment signature", description = "Verifies HMAC SHA256 signature and updates payment status")
    public ResponseEntity<VerifyPaymentResponse> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {
        return ResponseEntity.ok(paymentVerificationService.verifyPayment(request));
    }

    @PostMapping("/payments/webhook")
    @Operation(summary = "Razorpay webhook endpoint", description = "Receives and processes Razorpay webhook events")
    public ResponseEntity<WebhookResponse> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature
    ) {
        return ResponseEntity.ok(webhookService.processWebhook(payload, signature));
    }

    @GetMapping("/payments/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details from MongoDB")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentVerificationService.getPaymentByPaymentId(paymentId));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves order details from MongoDB")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(razorpayOrderService.getOrderByOrderId(orderId));
    }
}

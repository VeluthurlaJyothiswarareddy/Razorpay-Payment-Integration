package com.razorpay.integration.service;

import com.razorpay.integration.dto.request.VerifyPaymentRequest;
import com.razorpay.integration.dto.response.PaymentResponse;
import com.razorpay.integration.dto.response.VerifyPaymentResponse;
import com.razorpay.integration.entity.OrderDocument;
import com.razorpay.integration.entity.OrderStatus;
import com.razorpay.integration.entity.PaymentDocument;
import com.razorpay.integration.entity.PaymentStatus;
import com.razorpay.integration.exception.PaymentVerificationException;
import com.razorpay.integration.exception.ResourceNotFoundException;
import com.razorpay.integration.repository.OrderRepository;
import com.razorpay.integration.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final SignatureService signatureService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayOrderService razorpayOrderService;

    public VerifyPaymentResponse verifyPayment(VerifyPaymentRequest request) {
        OrderDocument order = orderRepository.findByOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + request.getRazorpayOrderId()));

        boolean isValid = signatureService.verifyPaymentSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            log.warn("Invalid payment signature for order: {}", request.getRazorpayOrderId());
            throw new PaymentVerificationException("Invalid payment signature");
        }

        PaymentDocument payment = paymentRepository.findByPaymentId(request.getRazorpayPaymentId())
                .orElseGet(() -> PaymentDocument.builder()
                        .paymentId(request.getRazorpayPaymentId())
                        .orderId(request.getRazorpayOrderId())
                        .amount(order.getAmount())
                        .currency(order.getCurrency())
                        .build());

        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setSignature(request.getRazorpaySignature());
        PaymentDocument savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        log.info("Payment verified successfully: {}", savedPayment.getPaymentId());

        return VerifyPaymentResponse.builder()
                .verified(true)
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrderId())
                .amount(savedPayment.getAmount())
                .currency(savedPayment.getCurrency())
                .status(savedPayment.getStatus())
                .message("Payment verified successfully")
                .verifiedAt(Instant.now())
                .build();
    }

    public PaymentResponse getPaymentByPaymentId(String paymentId) {
        PaymentDocument payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        return mapToResponse(payment);
    }

    public PaymentDocument upsertPaymentFromWebhook(
            String paymentId,
            String orderId,
            Long amount,
            String currency,
            PaymentStatus status,
            String method,
            String email,
            String contact
    ) {
        PaymentDocument payment = paymentRepository.findByPaymentId(paymentId)
                .orElse(PaymentDocument.builder()
                        .paymentId(paymentId)
                        .orderId(orderId)
                        .build());

        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(status);
        payment.setMethod(method);
        payment.setEmail(email);
        payment.setContact(contact);

        return paymentRepository.save(payment);
    }

    private PaymentResponse mapToResponse(PaymentDocument payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .email(payment.getEmail())
                .contact(payment.getContact())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

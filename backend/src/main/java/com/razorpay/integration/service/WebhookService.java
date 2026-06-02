package com.razorpay.integration.service;

import com.razorpay.integration.dto.response.WebhookResponse;
import com.razorpay.integration.entity.PaymentStatus;
import com.razorpay.integration.exception.WebhookVerificationException;
import com.razorpay.integration.util.RazorpayJsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final SignatureService signatureService;
    private final PaymentVerificationService paymentVerificationService;
    private final RazorpayOrderService razorpayOrderService;

    public WebhookResponse processWebhook(String payload, String signature) {
        signatureService.verifyWebhookSignature(payload, signature);

        JSONObject webhookPayload = new JSONObject(payload);
        String event = webhookPayload.optString("event");
        JSONObject payloadEntity = webhookPayload.optJSONObject("payload");

        log.info("Processing webhook event: {}", event);

        return switch (event) {
            case "payment.captured" -> handlePaymentCaptured(payloadEntity);
            case "payment.failed" -> handlePaymentFailed(payloadEntity);
            case "order.paid" -> handleOrderPaid(payloadEntity);
            case "refund.created" -> handleRefundCreated(payloadEntity);
            default -> {
                log.warn("Unhandled webhook event: {}", event);
                yield WebhookResponse.builder()
                        .status("ignored")
                        .event(event)
                        .message("Event type not handled")
                        .build();
            }
        };
    }

    private WebhookResponse handlePaymentCaptured(JSONObject payloadEntity) {
        JSONObject paymentEntity = getNestedEntity(payloadEntity, "payment");
        if (paymentEntity == null) {
            throw new WebhookVerificationException("Missing payment entity in webhook payload");
        }

        JSONObject payment = paymentEntity.getJSONObject("entity");
        String paymentId = payment.getString("id");
        String orderId = payment.optString("order_id", null);
        Long amount = RazorpayJsonUtils.getLong(payment, "amount");
        String currency = payment.getString("currency");
        String method = payment.optString("method", null);
        String email = payment.optString("email", null);
        String contact = payment.optString("contact", null);

        paymentVerificationService.upsertPaymentFromWebhook(
                paymentId, orderId, amount, currency,
                PaymentStatus.CAPTURED, method, email, contact
        );

        if (orderId != null) {
            razorpayOrderService.markOrderPaid(orderId);
        }

        return WebhookResponse.builder()
                .status("success")
                .event("payment.captured")
                .message("Payment captured and recorded")
                .build();
    }

    private WebhookResponse handlePaymentFailed(JSONObject payloadEntity) {
        JSONObject paymentEntity = getNestedEntity(payloadEntity, "payment");
        if (paymentEntity == null) {
            throw new WebhookVerificationException("Missing payment entity in webhook payload");
        }

        JSONObject payment = paymentEntity.getJSONObject("entity");
        String paymentId = payment.getString("id");
        String orderId = payment.optString("order_id", null);
        Long amount = RazorpayJsonUtils.getLong(payment, "amount");
        String currency = payment.getString("currency");

        paymentVerificationService.upsertPaymentFromWebhook(
                paymentId, orderId, amount, currency,
                PaymentStatus.FAILED, null, null, null
        );

        if (orderId != null) {
            razorpayOrderService.markOrderFailed(orderId);
        }

        return WebhookResponse.builder()
                .status("success")
                .event("payment.failed")
                .message("Payment failure recorded")
                .build();
    }

    private WebhookResponse handleOrderPaid(JSONObject payloadEntity) {
        JSONObject orderEntity = getNestedEntity(payloadEntity, "order");
        if (orderEntity == null) {
            throw new WebhookVerificationException("Missing order entity in webhook payload");
        }

        JSONObject order = orderEntity.getJSONObject("entity");
        String orderId = order.getString("id");

        razorpayOrderService.markOrderPaid(orderId);

        return WebhookResponse.builder()
                .status("success")
                .event("order.paid")
                .message("Order marked as paid")
                .build();
    }

    private WebhookResponse handleRefundCreated(JSONObject payloadEntity) {
        JSONObject refundEntity = getNestedEntity(payloadEntity, "refund");
        if (refundEntity == null) {
            throw new WebhookVerificationException("Missing refund entity in webhook payload");
        }

        JSONObject refund = refundEntity.getJSONObject("entity");
        String paymentId = refund.getString("payment_id");
        String orderId = refund.optString("order_id", null);
        Long amount = RazorpayJsonUtils.getLong(refund, "amount");
        String currency = refund.optString("currency", "INR");

        paymentVerificationService.upsertPaymentFromWebhook(
                paymentId, orderId, amount, currency,
                PaymentStatus.REFUNDED, null, null, null
        );

        return WebhookResponse.builder()
                .status("success")
                .event("refund.created")
                .message("Refund recorded")
                .build();
    }

    private JSONObject getNestedEntity(JSONObject payloadEntity, String key) {
        if (payloadEntity == null || !payloadEntity.has(key)) {
            return null;
        }
        return payloadEntity.getJSONObject(key);
    }
}

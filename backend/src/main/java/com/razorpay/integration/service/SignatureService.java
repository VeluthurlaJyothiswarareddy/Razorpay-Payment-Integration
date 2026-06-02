package com.razorpay.integration.service;

import com.razorpay.integration.config.RazorpayProperties;
import com.razorpay.integration.exception.PaymentVerificationException;
import com.razorpay.integration.exception.WebhookVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureService {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final RazorpayProperties razorpayProperties;

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String expectedSignature = hmacSha256(payload, razorpayProperties.getKeySecret());
            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception ex) {
            log.error("Payment signature verification failed", ex);
            throw new PaymentVerificationException("Failed to verify payment signature");
        }
    }

    public void verifyWebhookSignature(String payload, String signature) {
        try {
            String expectedSignature = hmacSha256(payload, razorpayProperties.getWebhookSecret());
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            )) {
                throw new WebhookVerificationException("Invalid webhook signature");
            }
        } catch (WebhookVerificationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Webhook signature verification failed", ex);
            throw new WebhookVerificationException("Failed to verify webhook signature");
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256
        );
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

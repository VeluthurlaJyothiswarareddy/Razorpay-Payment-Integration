package com.razorpay.integration.repository;

import com.razorpay.integration.entity.PaymentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<PaymentDocument, String> {

    Optional<PaymentDocument> findByPaymentId(String paymentId);

    Optional<PaymentDocument> findByOrderId(String orderId);
}

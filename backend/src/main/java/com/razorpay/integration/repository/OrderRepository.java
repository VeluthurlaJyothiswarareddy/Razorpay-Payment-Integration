package com.razorpay.integration.repository;

import com.razorpay.integration.entity.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    Optional<OrderDocument> findByOrderId(String orderId);
}

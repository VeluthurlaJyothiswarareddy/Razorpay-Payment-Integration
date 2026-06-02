package com.razorpay.integration.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.integration.config.RazorpayProperties;
import com.razorpay.integration.dto.request.CreateOrderRequest;
import com.razorpay.integration.dto.response.CreateOrderResponse;
import com.razorpay.integration.dto.response.OrderResponse;
import com.razorpay.integration.entity.OrderDocument;
import com.razorpay.integration.entity.OrderStatus;
import com.razorpay.integration.exception.RazorpayServiceException;
import com.razorpay.integration.exception.ResourceNotFoundException;
import com.razorpay.integration.repository.OrderRepository;
import com.razorpay.integration.util.RazorpayJsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayOrderService {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;
    private final OrderRepository orderRepository;

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount());
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", resolveReceipt(request.getReceipt()));
            orderRequest.put("payment_capture", 1);

            if (request.getNotes() != null && !request.getNotes().isBlank()) {
                JSONObject notes = new JSONObject();
                notes.put("description", request.getNotes());
                orderRequest.put("notes", notes);
            }

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            OrderDocument orderDocument = OrderDocument.builder()
                    .orderId(razorpayOrder.get("id"))
                    .amount(RazorpayJsonUtils.toLong(razorpayOrder.get("amount")))
                    .currency(razorpayOrder.get("currency"))
                    .status(OrderStatus.CREATED)
                    .receipt(razorpayOrder.get("receipt"))
                    .build();

            OrderDocument saved = orderRepository.save(orderDocument);
            log.info("Created Razorpay order: {}", saved.getOrderId());

            return CreateOrderResponse.builder()
                    .orderId(saved.getOrderId())
                    .amount(saved.getAmount())
                    .currency(saved.getCurrency())
                    .status(saved.getStatus())
                    .keyId(razorpayProperties.getKeyId())
                    .receipt(saved.getReceipt())
                    .createdAt(saved.getCreatedAt())
                    .build();

        } catch (RazorpayException ex) {
            log.error("Failed to create Razorpay order", ex);
            throw new RazorpayServiceException("Failed to create Razorpay order: " + ex.getMessage(), ex);
        }
    }

    public OrderResponse getOrderByOrderId(String orderId) {
        OrderDocument order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        return mapToResponse(order);
    }

    public void markOrderPaid(String orderId) {
        orderRepository.findByOrderId(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            log.info("Order marked as PAID: {}", orderId);
        });
    }

    public void markOrderFailed(String orderId) {
        orderRepository.findByOrderId(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            log.info("Order marked as FAILED: {}", orderId);
        });
    }

    private String resolveReceipt(String receipt) {
        if (receipt != null && !receipt.isBlank()) {
            return receipt;
        }
        return "rcpt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private OrderResponse mapToResponse(OrderDocument order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .status(order.getStatus())
                .receipt(order.getReceipt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

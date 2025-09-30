package com.orderservice.service;

import com.orderservice.client.InventoryClient;
import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.OrderResponse;
import com.orderservice.model.Order;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {
        var inventoryProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (inventoryProductInStock) {
            Order order = Order.builder()
                    .orderNumber(orderRequest.orderNumber())
                    .price(orderRequest.price())
                    .skuCode(orderRequest.skuCode())
                    .quantity(orderRequest.quantity())
                    .build();
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Product with skuCode " + orderRequest.skuCode() + " is not in stock");
        }
    }

    public List<OrderResponse> getAllPlacedOrders() {
        return orderRepository.findAll().stream()
                .map(this::toOrderResponse).collect(Collectors.toList());
    }

    public OrderResponse findById(Long id) {
        return orderRepository.findById(id).stream()
                .map(this::toOrderResponse)
                .findFirst()
                .orElse(null);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getSkuCode(), order.getPrice(), order.getQuantity());
    }
}

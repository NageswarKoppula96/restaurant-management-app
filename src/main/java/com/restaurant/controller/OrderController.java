package com.restaurant.controller;

import com.restaurant.dto.OrderResponse;
import com.restaurant.dto.CreateOrderByNameRequest;
import com.restaurant.dto.CreateOrderRequest;
import com.restaurant.dto.UpdateStatusRequest;
import com.restaurant.entity.*;
import com.restaurant.mapper.OrderMapper;
import com.restaurant.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Controller for managing order-related operations.
 * Provides endpoints for creating, retrieving, and updating orders.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Order Management", description = "APIs for managing restaurant orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping("/by-name")
    @Operation(description = "Creates a new order with the specified menu item names for a customer")
    public ResponseEntity<?> createOrderByName(@Valid @RequestBody CreateOrderByNameRequest request) {
        try {
            System.out.println("Received order request: " + request);
            if (request == null) {
                return ResponseEntity.badRequest().body("Request cannot be null");
            }
            if (request.getCustomerPhone() == null) {
                return ResponseEntity.badRequest().body("Customer phone number is required");
            }
            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Order must contain at least one item");
            }
            
            try {
                Order order = orderService.createOrderByName(request.getCustomerPhone(), request.getItems());
                OrderResponse response = orderMapper.toOrderResponse(order);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (ResponseStatusException e) {
                return ResponseEntity.status(e.getStatusCode())
                    .body(e.getReason());
            } catch (Exception e) {
                System.err.println("Error creating order: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your order: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred");
        }
    }
    
    @PostMapping
    @Operation(description = "Creates a new order with the specified items for a customer")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            if (request == null || request.getCustomerPhone() == null || request.getOrderItems() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Order order = orderService.createOrder(request.getCustomerPhone(), request.getOrderItems());
            OrderResponse response = orderMapper.toOrderResponse(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(description = "Retrieves a specific order by its unique identifier")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(orderMapper::toOrderResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/phone/{phoneNumber}")
    @Operation(description = "Retrieves all orders for a specific customer by phone number")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerPhone(@PathVariable String phoneNumber) {
        List<OrderResponse> responses = orderService.getOrdersByCustomerPhone(phoneNumber).stream()
                .map(orderMapper::toOrderResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/customer/phone/{phoneNumber}/latest")
    @Operation(description = "Retrieves the latest order for a specific customer by phone number")
    public ResponseEntity<OrderResponse> getLatestOrderByCustomerPhone(@PathVariable String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        return orderService.getLatestOrderByCustomerPhone(phoneNumber)
                .map(orderMapper::toOrderResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(description = "Retrieves all orders in the system")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = orderService.getAllOrders().stream()
                .map(orderMapper::toOrderResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    @Operation(description = "Updates the status of an existing order")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id, 
            @RequestBody UpdateStatusRequest request) {
        try {
            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
            OrderResponse response = orderMapper.toOrderResponse(updatedOrder);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(description = "Retrieves all orders with the specified status")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<OrderResponse> responses = orderService.getOrdersByStatus(orderStatus).stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}/count")
    @Operation(description = "Counts the number of orders with the specified status")
    public ResponseEntity<Long> getOrderCountByStatus(
            @PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            Long count = orderService.getOrderCountByStatus(orderStatus);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

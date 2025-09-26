package com.restaurant.service;

import com.restaurant.dto.OrderItemByNameRequest;
import com.restaurant.dto.OrderItemRequest;
import com.restaurant.entity.Customer;
import com.restaurant.entity.MenuItem;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.entity.OrderStatus;
import com.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final MenuService menuService;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository,
                       CustomerService customerService,
                       MenuService menuService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.menuService = menuService;
    }

    @Transactional
    public Order createOrderByName(String customerPhone, List<OrderItemByNameRequest> orderItemRequests) {
        // Validate input
        if (orderItemRequests == null || orderItemRequests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        // Check for duplicate menu items in the request
        long uniqueMenuItems = orderItemRequests.stream()
                .map(OrderItemByNameRequest::getMenuItemName)
                .distinct()
                .count();
                
        if (uniqueMenuItems != orderItemRequests.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate menu items found in the order");
        }

        // Get customer
        Customer customer = customerService.getCustomerByPhone(customerPhone)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Customer with phone number %s is not registered", customerPhone)
                ));

        // Create order
        Order order = new Order(customer);

        // Add order items
        for (OrderItemByNameRequest itemRequest : orderItemRequests) {
            MenuItem menuItem = menuService.getMenuItemByName(itemRequest.getMenuItemName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            String.format("Menu item with name '%s' not found", itemRequest.getMenuItemName())
                    ));

            if (menuItem.getAvailable() == null || !menuItem.getAvailable()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("Menu item '%s' is currently not available", menuItem.getName())
                );
            }

            if (itemRequest.getQuantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("Invalid quantity %d for menu item %s",
                                itemRequest.getQuantity(),
                                menuItem.getName())
                );
            }

            // Create and add order item using the helper method
            OrderItem orderItem = new OrderItem(order, menuItem, itemRequest.getQuantity());
            order.addOrderItem(orderItem);
        }

        // Calculate total amount
        order.calculateTotalAmount();
        
        // Save and return the order
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(String customerPhone, List<OrderItemRequest> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        // Get customer
        Customer customer = customerService.getCustomerByPhone(customerPhone)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Customer with phone number %s is not registered", customerPhone)
                ));

        // Create order
        Order order = new Order(customer);

        // Add order items
        for (OrderItemRequest itemRequest : orderItems) {
            MenuItem menuItem = menuService.getMenuItemById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            String.format("Menu item with id %d not found", itemRequest.getMenuItemId())));

            if (itemRequest.getQuantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("Invalid quantity %d for menu item %s",
                                itemRequest.getQuantity(),
                                menuItem.getName()));
            }

            OrderItem orderItem = new OrderItem(order, menuItem, itemRequest.getQuantity());
            order.getOrderItems().add(orderItem);
        }

        order.calculateTotalAmount();

        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByCustomerPhone(String phoneNumber) {
        return orderRepository.findByCustomerPhone(phoneNumber);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateOrderStatus(String id, OrderStatus status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            order.setUpdatedAt(java.time.LocalDateTime.now());
            return orderRepository.save(order);
        }).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("Order not found with id: %s", id)));
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    public Optional<Order> getLatestOrderByCustomerPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Phone number cannot be empty"
            );
        }
        logger.info("Looking for latest order for phone number: {}", phoneNumber);
        Optional<Order> order = orderRepository.findLatestOrderByCustomerPhone(phoneNumber);
        logger.info("Found order for phone {}: {}", phoneNumber, order.isPresent() ? order.get().getId() : "Not found");
        return order;
    }
}

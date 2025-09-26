package com.restaurant.controller;

import com.restaurant.entity.Customer;
import com.restaurant.entity.Order;
import com.restaurant.repository.CustomerRepository;
import com.restaurant.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public DebugController(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @GetMapping("/customers/phone/{phoneNumber}")
    public ResponseEntity<Customer> getCustomerByPhone(@PathVariable String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/orders/customer/phone/{phoneNumber}")
    public ResponseEntity<List<Order>> getOrdersByCustomerPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(orderRepository.findByCustomerPhone(phoneNumber));
    }
}

package com.restaurant.controller;

// These imports are used in method return types and parameters
import com.restaurant.entity.Customer; // Used in getCustomerByPhone return type
import com.restaurant.entity.Order; // Used in getOrdersByCustomerPhone return type
import com.restaurant.repository.CustomerRepository;
import com.restaurant.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Used in method return types
import java.util.List; // Used in getAllCustomers and getOrdersByCustomerPhone return types

/**
 * Debug controller for development and troubleshooting purposes.
 * WARNING: This controller should be disabled in production environments.
 */
@RestController
@RequestMapping("/api/debug")
@Tag(name = "Debug", description = "Debug endpoints for development and troubleshooting")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    
    @Value("${app.debug.enabled:false}")
    private boolean debugEnabled;

    @Autowired
    public DebugController(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/customers")
    @Operation(
        summary = "Get all customers",
        description = "Retrieves a list of all customers (DEBUG ONLY)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers"),
            @ApiResponse(responseCode = "403", description = "Debug endpoints are disabled")
        }
    )
    public ResponseEntity<?> getAllCustomers() {
        if (!debugEnabled) {
            logger.warn("Attempted to access debug endpoint /api/debug/customers while debug is disabled");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Debug endpoints are disabled");
        }
        logger.debug("Fetching all customers");
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @GetMapping("/customers/phone/{phoneNumber}")
    @Operation(
        summary = "Get customer by phone",
        description = "Retrieves a customer by phone number (DEBUG ONLY)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Debug endpoints are disabled")
        }
    )
    public ResponseEntity<?> getCustomerByPhone(
            @Parameter(description = "Phone number of the customer to be retrieved") 
            @PathVariable String phoneNumber) {
        if (!debugEnabled) {
            logger.warn("Attempted to access debug endpoint /api/debug/customers/phone/{} while debug is disabled", phoneNumber);
            return ResponseEntity.status(403).body("Debug endpoints are disabled");
        }
        logger.debug("Fetching customer with phone number: {}", phoneNumber);
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.debug("No customer found with phone number: {}", phoneNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/orders")
    @Operation(
        summary = "Get all orders",
        description = "Retrieves a list of all orders (DEBUG ONLY)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "403", description = "Debug endpoints are disabled")
        }
    )
    public ResponseEntity<?> getAllOrders() {
        if (!debugEnabled) {
            logger.warn("Attempted to access debug endpoint /api/debug/orders while debug is disabled");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Debug endpoints are disabled");
        }
        logger.debug("Fetching all orders");
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/orders/customer/phone/{phoneNumber}")
    @Operation(
        summary = "Get orders by customer phone",
        description = "Retrieves all orders for a customer by phone number (DEBUG ONLY)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer orders"),
            @ApiResponse(responseCode = "403", description = "Debug endpoints are disabled")
        }
    )
    public ResponseEntity<?> getOrdersByCustomerPhone(
            @Parameter(description = "Phone number of the customer") 
            @PathVariable String phoneNumber) {
        if (!debugEnabled) {
            logger.warn("Attempted to access debug endpoint /api/debug/orders/customer/phone/{} while debug is disabled", phoneNumber);
            return ResponseEntity.status(403).body("Debug endpoints are disabled");
        }
        logger.debug("Fetching orders for customer with phone number: {}", phoneNumber);
        return ResponseEntity.ok(orderRepository.findByCustomerPhone(phoneNumber));
    }
}

package com.restaurant.controller;

import com.restaurant.entity.Customer;
import com.restaurant.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing customer-related operations.
 * Provides endpoints for creating, retrieving, updating, and deleting customer records.
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", 
    allowedHeaders = "*", 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, 
    allowCredentials = "false", 
    maxAge = 3600)
@Tag(name = "Customer Management", description = "APIs for managing restaurant customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(description = "Registers a new customer in the system")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(description = "Retrieves a specific customer by their unique identifier")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(description = "Retrieves a customer by their email address")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(description = "Retrieves a customer by their phone number")
    public ResponseEntity<Customer> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {
        return customerService.getCustomerByPhone(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(description = "Retrieves a list of all registered customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    @Operation(description = "Updates an existing customer's information")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody Customer customerDetails) {
        return customerService.getCustomerById(id)
                .map(existingCustomer -> updateExistingCustomer(existingCustomer, customerDetails, id))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<Customer> updateExistingCustomer(Customer existingCustomer, Customer customer, Long id) {
        customer.setId(id);
        try {
            Customer updatedCustomer = customerService.saveCustomer(customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Removes a customer from the system")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> {
                    customerService.deleteCustomer(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/email/{email}")
    @Operation(description = "Checks if a customer with the given email address exists in the system")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = customerService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/exists/phone/{phoneNumber}")
    @Operation(description = "Checks if a customer with the given phone number exists in the system")
    public ResponseEntity<?> existsByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            boolean exists = customerService.existsByPhoneNumber(phoneNumber);
            String message = exists ? "Customer with the provided phone number exists." 
                                  : "No customer found with the provided phone number.";
            
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("message", message);
            response.put("phoneNumber", phoneNumber);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while checking phone number");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

package com.restaurant.controller;

import com.restaurant.entity.Customer;
import com.restaurant.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Operation(summary = "Register a new customer", 
              description = "Creates a new customer with the provided details. Both email and phone number must be unique.")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Customer registered successfully");
            response.put("data", savedCustomer);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            
            if (e.getMessage() != null && e.getMessage().contains("EMAIL")) {
                errorResponse.put("message", "Email already exists");
            } else if (e.getMessage() != null && e.getMessage().contains("PHONE_NUMBER")) {
                errorResponse.put("message", "Phone number already exists");
            } else {
                errorResponse.put("message", "A customer with the provided details already exists");
            }
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (ConstraintViolationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("message", "Validation failed: " + e.getConstraintViolations().stream()
                    .map(cv -> cv.getMessage())
                    .collect(Collectors.joining(", ")));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/email/{email}")
    @Operation(description = "Retrieves a customer by their email address")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable String email) {
        try {
            Optional<Customer> customer = customerService.getCustomerByEmail(email);
            if (customer.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("data", customer.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Customer not found with email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error retrieving customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(description = "Retrieves a customer by their phone number")
    public ResponseEntity<?> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            Optional<Customer> customer = customerService.getCustomerByPhone(phoneNumber);
            if (customer.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("data", customer.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Customer not found with phone number: " + phoneNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error retrieving customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    @Operation(description = "Retrieves a list of all registered customers")
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", customers.size());
            response.put("data", customers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error retrieving customers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @Operation(description = "Updates an existing customer's information")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody Customer customerDetails) {
        try {
            Optional<Customer> existingCustomer = customerService.getCustomerById(id);
            if (existingCustomer.isPresent()) {
                // Preserve the original creation date
                customerDetails.setCreatedAt(existingCustomer.get().getCreatedAt());
                customerDetails.setId(id);
                
                try {
                    Customer updatedCustomer = customerService.saveCustomer(customerDetails);
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Customer updated successfully");
                    response.put("data", updatedCustomer);
                    return ResponseEntity.ok(response);
                } catch (DataIntegrityViolationException e) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("message", "A customer with the provided email or phone number already exists");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                }
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Customer not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error updating customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Removes a customer from the system")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            Optional<Customer> customer = customerService.getCustomerById(id);
            if (customer.isPresent()) {
                customerService.deleteCustomer(id);
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Customer deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Customer not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error deleting customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/exists/email/{email}")
    @Operation(description = "Checks if a customer with the given email address exists in the system")
    public ResponseEntity<?> existsByEmail(@PathVariable String email) {
        try {
            boolean exists = customerService.existsByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("exists", exists);
            response.put("email", email);
            response.put("message", exists ? "Email is already registered" : "Email is available");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error checking email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/exists/phone/{phoneNumber}")
    @Operation(description = "Checks if a customer with the given phone number exists in the system")
    public ResponseEntity<?> existsByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            boolean exists = customerService.existsByPhoneNumber(phoneNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("exists", exists);
            response.put("phoneNumber", phoneNumber);
            response.put("message", exists ? "Phone number is already registered" : "Phone number is available");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error checking phone number: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @DeleteMapping("/phone/{phoneNumber}")
    @Operation(
        summary = "Delete customer by phone number",
        description = "Deletes a customer from the system using their phone number. The phone number should exist in the system.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found with the given phone number"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<?> deleteCustomerByPhoneNumber(
            @PathVariable String phoneNumber) {
        
        try {
            // First check if customer exists
            Optional<Customer> customerOpt = customerService.getCustomerByPhone(phoneNumber);
            
            if (customerOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "No customer found with phone number: " + phoneNumber);
                errorResponse.put("phoneNumber", phoneNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Delete the customer
            customerService.deleteCustomer(customerOpt.get().getId());
            
            // Build success response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Customer deleted successfully");
            response.put("phoneNumber", phoneNumber);
            response.put("deletedCustomerId", customerOpt.get().getId());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error deleting customer: " + e.getMessage());
            errorResponse.put("phoneNumber", phoneNumber);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

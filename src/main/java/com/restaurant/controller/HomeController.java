package com.restaurant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the root endpoint that provides information about the API.
 * Serves as a welcome page and API documentation entry point.
 */
@RestController
@Tag(name = "Home", description = "Root endpoint and API information")
public class HomeController {

    @GetMapping("/")
    @Operation(description = "Returns a welcome message and a list of available API endpoints")
    public String home() {
        return """
            Welcome to Restaurant Management System!

            Menu Endpoints:
            • GET    /api/menu                    - Get all menu items
            • GET    /api/menu/categories         - Get all menu categories
            • GET    /api/menu/category/{category} - Get menu items by category
            • GET    /api/menu/{id}               - Get menu item by ID
            • POST   /api/menu                    - Create new menu item
            • PUT    /api/menu/{id}               - Update menu item
            • DELETE /api/menu/{id}               - Delete menu item

            Order Endpoints:
            • POST   /api/orders                  - Create a new order
            • GET    /api/orders                  - Get all orders
            • GET    /api/orders/{id}             - Get order by ID
            • GET    /api/orders/customer/{email} - Get orders by customer email
            • PUT    /api/orders/{id}/status      - Update order status
            • GET    /api/orders/status/{status}  - Get orders by status

            Customer Endpoints:
            • GET    /api/customers                     - Get all customers
            • POST   /api/customers                     - Create a new customer
            • GET    /api/customers/{id}                - Get customer by ID
            • GET    /api/customers/email/{email}       - Get customer by email
            • GET    /api/customers/phone/{phone}       - Get customer by phone
            • PUT    /api/customers/{id}                - Update customer
            • DELETE /api/customers/{id}                - Delete customer by ID
            • DELETE /api/customers/phone/{phoneNumber}  - Delete customer by phone number (10 digits)
            • GET    /api/customers/exists/email/{email} - Check if email exists
            • GET    /api/customers/exists/phone/{phone} - Check if phone exists

            Documentation:
            • Swagger UI: /swagger-ui.html
            • Derby Database: Available at startup (Database: restaurantdb, Username: app, Password: empty) - For development only
            """.replaceAll("            ", "");
    }
}

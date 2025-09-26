package com.restaurant.config;

import com.restaurant.entity.MenuItem;
import com.restaurant.entity.Customer;
import com.restaurant.service.MenuService;
import com.restaurant.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final MenuService menuService;
    private final CustomerService customerService;

    @Autowired
    public DataLoader(MenuService menuService, CustomerService customerService) {
        this.menuService = menuService;
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create sample customers if none exist
        if (customerService.getAllCustomers().isEmpty()) {
            Customer customer1 = new Customer("John Doe", "john@example.com", "123-456-7890");
            Customer customer2 = new Customer("Jane Smith", "jane@example.com", "098-765-4321");
            customerService.saveCustomer(customer1);
            customerService.saveCustomer(customer2);
        }

        // Create sample menu items if none exist
        if (menuService.getAllMenuItems().isEmpty()) {
            MenuItem item1 = new MenuItem(
                "Margherita Pizza",
                "Classic pizza with tomato sauce, mozzarella, and basil",
                new BigDecimal("12.99"),
                "Pizza",
                true
            );

            MenuItem item2 = new MenuItem(
                "Pepperoni Pizza",
                "Pizza topped with pepperoni slices and mozzarella cheese",
                new BigDecimal("14.99"),
                "Pizza",
                true
            );

            MenuItem item3 = new MenuItem(
                "Caesar Salad",
                "Fresh romaine lettuce with Caesar dressing, croutons, and parmesan",
                new BigDecimal("9.99"),
                "Salad",
                true
            );

            MenuItem item4 = new MenuItem(
                "Chicken Burger",
                "Grilled chicken breast with lettuce, tomato, and special sauce",
                new BigDecimal("10.99"),
                "Burger",
                true
            );

            MenuItem item5 = new MenuItem(
                "Chocolate Brownie",
                "Warm chocolate brownie with vanilla ice cream",
                new BigDecimal("6.99"),
                "Dessert",
                true
            );

            MenuItem item6 = new MenuItem(
                "French Fries",
                "Crispy golden french fries seasoned with salt",
                new BigDecimal("4.99"),
                "Sides",
                true
            );

            MenuItem item7 = new MenuItem(
                "Pasta Carbonara",
                "Spaghetti with creamy sauce, pancetta, and parmesan",
                new BigDecimal("14.99"),
                "Pasta",
                true
            );

            MenuItem item8 = new MenuItem(
                "Iced Tea",
                "Refreshing iced tea with lemon",
                new BigDecimal("2.99"),
                "Beverages",
                true
            );

            // Save all menu items
            menuService.saveMenuItem(item1);
            menuService.saveMenuItem(item2);
            menuService.saveMenuItem(item3);
            menuService.saveMenuItem(item4);
            menuService.saveMenuItem(item5);
            menuService.saveMenuItem(item6);
            menuService.saveMenuItem(item7);
            menuService.saveMenuItem(item8);
        }
    }
}

package com.restaurant.controller;

import com.restaurant.entity.MenuItem;
import com.restaurant.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// SpringDoc OpenAPI annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
@Tag(name = "Menu Management", description = "APIs for managing restaurant menu items")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @Operation(description = "Retrieves a list of all available menu items in the restaurant")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/categories")
    @Operation(description = "Retrieves a list of all unique menu categories available in the restaurant")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = menuService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category/{category}")
    @Operation(description = "Retrieves all menu items that belong to the specified category")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable String category) {
        List<MenuItem> menuItems = menuService.getMenuItemsByCategory(category);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{id}")
    @Operation(description = "Retrieves a specific menu item by its unique identifier")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return menuService.getMenuItemById(id)
                .map(menuItem -> ResponseEntity.ok(menuItem))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(description = "Adds a new menu item to the restaurant's menu")
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody MenuItem menuItem) {
        try {
            MenuItem savedMenuItem = menuService.saveMenuItem(menuItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMenuItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(description = "Updates an existing menu item with the provided details")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItem menuItemDetails) {
        return menuService.getMenuItemById(id)
                .map(existingMenuItem -> updateExistingMenuItem(existingMenuItem, menuItemDetails, id))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<MenuItem> updateExistingMenuItem(MenuItem existingMenuItem, MenuItem menuItem, Long id) {
        menuItem.setId(id);
        try {
            MenuItem updatedMenuItem = menuService.saveMenuItem(menuItem);
            return ResponseEntity.ok(updatedMenuItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Removes a menu item from the restaurant's menu")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        return menuService.getMenuItemById(id)
                .map(menuItem -> {
                    menuService.deleteMenuItem(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

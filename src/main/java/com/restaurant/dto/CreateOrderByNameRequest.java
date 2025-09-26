package com.restaurant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderByNameRequest {
    @NotNull(message = "Customer phone number is required")
    private String customerPhone;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemByNameRequest> items;
}

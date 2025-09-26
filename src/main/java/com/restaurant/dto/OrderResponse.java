package com.restaurant.dto;

import com.restaurant.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String orderNumber;
    private String customerName;
    private String customerPhone;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private String menuItemName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }
}

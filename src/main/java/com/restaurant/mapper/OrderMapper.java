package com.restaurant.mapper;

import com.restaurant.dto.OrderResponse;
import com.restaurant.entity.MenuItem;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getId());
        response.setCustomerName(order.getCustomer() != null ? order.getCustomer().getName() : "Walk-in Customer");
        response.setCustomerPhone(order.getCustomer() != null ? order.getCustomer().getPhoneNumber() : "");
        response.setOrderDate(order.getCreatedAt());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        if (order.getOrderItems() != null) {
            response.setItems(order.getOrderItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        MenuItem menuItem = orderItem.getMenuItem();
        return new OrderResponse.OrderItemResponse(
                menuItem != null ? menuItem.getName() : "",
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getTotalPrice()
        );
    }
}

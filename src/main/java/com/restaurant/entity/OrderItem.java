package com.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    @NotNull(message = "Menu item is required")
    private MenuItem menuItem;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public OrderItem(Order order, MenuItem menuItem, Integer quantity) {
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
        if (menuItem != null) {
            this.unitPrice = menuItem.getPrice();
            if (quantity != null) {
                this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void calculateTotalPrice() {
        if (unitPrice == null && menuItem != null) {
            unitPrice = menuItem.getPrice();
        }
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        if (menuItem != null) {
            this.unitPrice = menuItem.getPrice();
            if (this.quantity != null) {
                this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
            }
        }
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", menuItem=" + menuItem +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}

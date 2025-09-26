package com.restaurant.repository;

import com.restaurant.entity.Order;
import com.restaurant.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(@Param("customerId") Long customerId);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderItems(@Param("orderId") String orderId);
    
    @Query("SELECT o FROM Order o WHERE o.customer.phoneNumber = :phoneNumber")
    List<Order> findByCustomerPhone(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customer.phoneNumber = :phoneNumber ORDER BY o.createdAt DESC LIMIT 1")
    Optional<Order> findLatestOrderByCustomerPhone(@Param("phoneNumber") String phoneNumber);
}

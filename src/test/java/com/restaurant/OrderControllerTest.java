package com.restaurant;

import com.restaurant.controller.OrderController;
import com.restaurant.dto.OrderResponse;
import com.restaurant.entity.Customer;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderStatus;
import com.restaurant.mapper.OrderMapper;
import com.restaurant.repository.CustomerRepository;
import com.restaurant.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private static final String TEST_PHONE = "9951402390";
    private static final String TEST_ORDER_ID = "ORD12345";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Reset mocks before each test
        org.mockito.Mockito.reset(orderService, orderMapper, customerRepository);
    }

    @Test
    void getLatestOrderByCustomerPhone_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        Customer customer = new Customer("Test User", "test@example.com", TEST_PHONE);
        customer.setId(1L);
        
        Order order = new Order(customer);
        order.setId(TEST_ORDER_ID);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setCreatedAt(LocalDateTime.now());
        
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(TEST_ORDER_ID);
        orderResponse.setStatus(OrderStatus.PENDING);
        orderResponse.setTotalAmount(new BigDecimal("100.00"));
        
        when(orderService.getLatestOrderByCustomerPhone(TEST_PHONE)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.getLatestOrderByCustomerPhone(TEST_PHONE);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        
        OrderResponse responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(TEST_ORDER_ID, responseBody.getId(), "Order ID should match");
        assertEquals(OrderStatus.PENDING, responseBody.getStatus(), "Order status should be PENDING");
        assertEquals(0, new BigDecimal("100.00").compareTo(responseBody.getTotalAmount()), 
            "Total amount should match");
    }

    @Test
    void getLatestOrderByCustomerPhone_WhenNoOrderExists_ShouldReturnNotFound() {
        // Arrange
        when(orderService.getLatestOrderByCustomerPhone(TEST_PHONE)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<OrderResponse> response = orderController.getLatestOrderByCustomerPhone(TEST_PHONE);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status code should be NOT_FOUND");
        assertFalse(response.hasBody(), "Response should not have a body");
        assertNull(response.getBody(), "Response body should be null");
    }

    @Test
    void getLatestOrderByCustomerPhone_WhenPhoneNumberIsEmpty_ShouldReturnBadRequest() {
        // Act
        ResponseEntity<OrderResponse> response = orderController.getLatestOrderByCustomerPhone("");

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status code should be BAD_REQUEST");
        assertFalse(response.hasBody(), "Response should not have a body");
        assertNull(response.getBody(), "Response body should be null");
    }
}

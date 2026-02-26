package com.tby.api.service;

import com.tby.api.calculation.PriceCalculationContext;
import com.tby.api.calculation.PriceCalculatorChain;
import com.tby.api.dto.OrderMessage;
import com.tby.api.dto.OrderRequest;
import com.tby.api.dto.OrderResponse;
import com.tby.api.model.Order;
import com.tby.api.model.Product;
import com.tby.api.model.ProductCategory;
import com.tby.api.model.User;
import com.tby.api.repository.OrderRepository;
import com.tby.api.repository.UserRepository;
import com.tby.api.util.IdempotencyUtil;

import cn.hutool.core.lang.Snowflake;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductService productService;
    @Mock
    private Snowflake snowflake;
    @Mock
    private IdempotencyUtil idempotencyUtil;
    @Mock
    private RocketMQTemplate rocketMQTemplate;
    @Mock
    private PriceCalculatorChain priceCalculatorChain;

    @InjectMocks
    private OrderService orderService;

    // private User user;
    private Product product;
    private ProductCategory category;

    @BeforeEach
    void setUp() {
        // user = User.builder().userId(1L).username("testuser").build();
        category = ProductCategory.builder().categoryId(1L).categoryName("Food").taxRate(new BigDecimal("0.05"))
                .build();
        product = Product.builder().productId(1L).categoryId(1L).unitPrice(new BigDecimal("100.00")).build();
    }

    @Test
    void createOrder_ShouldCalculateTotalCostCorrectly() {
        // Arrange
        OrderRequest request = OrderRequest.builder()
                .userId(1L)
                .productId(1L)
                .orderAmount(2)
                // requestId is null, so it skips idempotency check in this test
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(productService.getProductEntityById(1L)).thenReturn(product);
        when(productService.getCategoryById(1L)).thenReturn(category);
        when(snowflake.nextId()).thenReturn(100L);
        // Mock chain returning the expected cost
        when(priceCalculatorChain.calculateTotalCost(any(PriceCalculationContext.class)))
                .thenReturn(new BigDecimal("210.0000"));

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertEquals(new BigDecimal("210.0000"), response.getTotalCost());
        assertEquals(100L, response.getOrderId());
        verify(rocketMQTemplate).convertAndSend(eq("order-create-topic"), any(OrderMessage.class));
        // Verify DB save is NOT called synchronously anymore
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void deleteOrder_ShouldThrowException_WhenUserIdMismatches() {
        // Arrange
        Order order = Order.builder().orderId(1L).userId(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.deleteOrder(1L, 2L); // Requesting user is 2, owner is 1
        });

        assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteOrder_ShouldSucceed_WhenUserIdMatches() {
        // Arrange
        Order order = Order.builder().orderId(1L).userId(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(1L, 1L);

        // Assert
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void patchOrder_ShouldThrowException_WhenUserIdMismatches() {
        // Arrange
        Order order = Order.builder().orderId(1L).userId(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderRequest request = OrderRequest.builder().userId(2L).orderAmount(5).build();

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.patchOrder(1L, request); // Requesting user is 2, owner is 1
        });

        assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
        verify(orderRepository, never()).save(any(Order.class));
    }
}

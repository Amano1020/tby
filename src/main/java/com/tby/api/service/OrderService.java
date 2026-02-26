package com.tby.api.service;

import cn.hutool.core.lang.Snowflake;
import com.tby.api.calculation.PriceCalculationContext;
import com.tby.api.calculation.PriceCalculatorChain;
import com.tby.api.dto.OrderMessage;
import com.tby.api.dto.OrderRequest;
import com.tby.api.dto.OrderResponse;
import com.tby.api.model.Order;
import com.tby.api.model.Product;
import com.tby.api.model.ProductCategory;
import com.tby.api.repository.OrderRepository;
import com.tby.api.repository.UserRepository;
import com.tby.api.util.IdempotencyUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final Snowflake snowflake;
    private final IdempotencyUtil idempotencyUtil;
    private final RocketMQTemplate rocketMQTemplate;
    private final PriceCalculatorChain priceCalculatorChain;

    // Topic for order creation
    private static final String ORDER_TOPIC = "order-create-topic";

    public OrderResponse createOrder(OrderRequest request) {
        // 1. Idempotency Check
        String requestId = request.getRequestId();
        boolean idempotencyCheckEnabled = requestId != null && !requestId.isEmpty();

        if (idempotencyCheckEnabled) {
            boolean isFirstRequest = idempotencyUtil.checkAndSetToken(requestId);
            if (!isFirstRequest) {
                throw new BusinessException(ErrorCode.DUPLICATE_ORDER);
            }
        }

        try {
            // 2. Validate User and Product
            if (!userRepository.existsById(request.getUserId())) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            Product product = productService.getProductEntityById(request.getProductId());
            ProductCategory category = productService.getCategoryById(product.getCategoryId());

            // 3. Generate ID and create message
            Long newOrderId = snowflake.nextId();

            OrderMessage message = OrderMessage.builder()
                    .orderId(newOrderId)
                    .userId(request.getUserId())
                    .productId(request.getProductId())
                    .orderAmount(request.getOrderAmount())
                    .build();

            // 4. Send to MQ for async DB writing
            rocketMQTemplate.convertAndSend(ORDER_TOPIC, message);

            // 5. Calculate Price using Chain of Responsibility
            PriceCalculationContext context = PriceCalculationContext.builder()
                    .product(product)
                    .category(category)
                    .orderAmount(request.getOrderAmount())
                    .build();
            BigDecimal totalCost = priceCalculatorChain.calculateTotalCost(context);

            return OrderResponse.builder()
                    .orderId(newOrderId)
                    .userId(request.getUserId())
                    .productId(request.getProductId())
                    .orderAmount(request.getOrderAmount())
                    .totalCost(totalCost)
                    .build();
        } catch (Exception e) {
            // Unlock token if process fails so the user can retry
            if (idempotencyCheckEnabled) {
                idempotencyUtil.unlockToken(requestId);
            }
            throw e; // rethrow to be handled by GlobalExceptionHandler
        }
    }

    @Transactional
    public OrderResponse patchOrder(Long orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (request.getOrderAmount() != null) {
            order.setOrderAmount(request.getOrderAmount());
        }

        order = orderRepository.save(order);

        // Single fetch map to response
        Product product = productService.getProductEntityById(order.getProductId());
        ProductCategory category = productService.getCategoryById(product.getCategoryId());

        PriceCalculationContext context = PriceCalculationContext.builder()
                .product(product)
                .category(category)
                .orderAmount(order.getOrderAmount())
                .build();
        BigDecimal totalCost = priceCalculatorChain.calculateTotalCost(context);

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .orderAmount(order.getOrderAmount())
                .totalCost(totalCost)
                .build();
    }

    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        orderRepository.deleteById(orderId);
    }

    public List<OrderResponse> getOrdersByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Collect all product IDs
        List<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .distinct()
                .collect(Collectors.toList());

        // 2. Batch fetch Products
        List<Product> products = productService.getProductsByIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        // 3. Collect all category IDs from those products
        List<Long> categoryIds = products.stream()
                .map(Product::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        // 4. Batch fetch Categories
        List<ProductCategory> categories = productService.getCategoriesByIds(categoryIds);
        Map<Long, ProductCategory> categoryMap = categories.stream()
                .collect(Collectors.toMap(ProductCategory::getCategoryId, c -> c));

        // 5. Build responses in memory utilizing Chain of Responsibility
        return orders.stream()
                .map(order -> {
                    Product product = productMap.get(order.getProductId());
                    ProductCategory category = categoryMap.get(product.getCategoryId());

                    PriceCalculationContext context = PriceCalculationContext.builder()
                            .product(product)
                            .category(category)
                            .orderAmount(order.getOrderAmount())
                            .build();
                    BigDecimal totalCost = priceCalculatorChain.calculateTotalCost(context);

                    return OrderResponse.builder()
                            .orderId(order.getOrderId())
                            .userId(order.getUserId())
                            .productId(order.getProductId())
                            .orderAmount(order.getOrderAmount())
                            .totalCost(totalCost)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

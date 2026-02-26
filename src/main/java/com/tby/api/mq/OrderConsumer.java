package com.tby.api.mq;

import com.tby.api.dto.OrderMessage;
import com.tby.api.model.Order;
import com.tby.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "order-create-topic", consumerGroup = "order-api-consumer-group")
public class OrderConsumer implements RocketMQListener<OrderMessage> {

    private final OrderRepository orderRepository;

    @Override
    public void onMessage(OrderMessage message) {
        log.info("Received new order message: {}", message.getOrderId());

        try {
            // Save to database
            Order order = Order.builder()
                    .orderId(message.getOrderId())
                    .userId(message.getUserId())
                    .productId(message.getProductId())
                    .orderAmount(message.getOrderAmount())
                    .build();

            orderRepository.save(order);
            log.info("Successfully persisted order: {}", message.getOrderId());

        } catch (Exception e) {
            log.error("Failed to process order message: {}", message.getOrderId(), e);
            // Optionally throw exception to force RocketMQ retry mechanism
            throw new RuntimeException("Error processing order message", e);
        }
    }
}

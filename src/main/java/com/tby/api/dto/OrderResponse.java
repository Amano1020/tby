package com.tby.api.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer orderAmount;
    private BigDecimal totalCost;
}

package com.tby.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer orderAmount;
}

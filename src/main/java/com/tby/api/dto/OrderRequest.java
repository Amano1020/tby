package com.tby.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long productId;
    @NotNull
    @Positive
    private Integer orderAmount;

    // Optional unique request ID from the client specifically for idempotency
    // checks
    private String requestId;
}

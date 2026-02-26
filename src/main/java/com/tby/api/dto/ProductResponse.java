package com.tby.api.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
}

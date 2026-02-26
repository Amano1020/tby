package com.tby.api.calculation;

import com.tby.api.model.Product;
import com.tby.api.model.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationContext {
    private Product product;
    private ProductCategory category;
    private Integer orderAmount;
}

package com.tby.api.calculation;

import java.math.BigDecimal;

public interface PriceCalculator {
    /**
     * Calculate the new price based on the current context and intermediate price.
     * 
     * @param context      Information about the product, category, and quantity.
     * @param currentPrice The running total price (to be modified).
     * @return The updated total price after this calculator's modifications.
     */
    BigDecimal calculate(PriceCalculationContext context, BigDecimal currentPrice);
}

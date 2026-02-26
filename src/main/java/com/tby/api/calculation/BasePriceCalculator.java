package com.tby.api.calculation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Calculates the base price: unitPrice * amount.
 * This should always be the first calculator in the chain.
 */
@Component
@Order(1) // Run first
public class BasePriceCalculator implements PriceCalculator {

    @Override
    public BigDecimal calculate(PriceCalculationContext context, BigDecimal currentPrice) {
        // If currentPrice is null or 0, we calculate the base price
        BigDecimal unitPrice = context.getProduct().getUnitPrice();
        BigDecimal amount = BigDecimal.valueOf(context.getOrderAmount());

        // Return base cost: unit_price * amount
        return unitPrice.multiply(amount);
    }
}

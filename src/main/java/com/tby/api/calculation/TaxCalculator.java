package com.tby.api.calculation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Calculates the tax and adds it to the current price.
 * This should run after discounts and base price calculations.
 */
@Component
@Order(100) // Run late in the chain
public class TaxCalculator implements PriceCalculator {

    @Override
    public BigDecimal calculate(PriceCalculationContext context, BigDecimal currentPrice) {
        BigDecimal taxRate = context.getCategory().getTaxRate();

        // Calculate tax amount on the current price
        BigDecimal taxAmount = currentPrice.multiply(taxRate);

        // Add tax to the current price
        return currentPrice.add(taxAmount);
    }
}

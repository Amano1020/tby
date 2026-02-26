package com.tby.api.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Assembles and executes the chain of PriceCalculators.
 * Spring automatically injects the list of beans implementing PriceCalculator,
 * sorted by their @Order annotation.
 */
@Component
@RequiredArgsConstructor
public class PriceCalculatorChain {

    // Spring injects all implementations ordered by @Order
    private final List<PriceCalculator> calculators;

    /**
     * Executes the chain of calculations.
     */
    public BigDecimal calculateTotalCost(PriceCalculationContext context) {
        BigDecimal currentPrice = BigDecimal.ZERO;

        for (PriceCalculator calculator : calculators) {
            currentPrice = calculator.calculate(context, currentPrice);
        }

        return currentPrice;
    }
}

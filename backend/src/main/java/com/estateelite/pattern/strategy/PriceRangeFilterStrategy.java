package com.estateelite.pattern.strategy;

import com.estateelite.model.Property;

import java.math.BigDecimal;

public class PriceRangeFilterStrategy implements SearchFilterStrategy {

    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    public PriceRangeFilterStrategy(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public boolean matches(Property property) {
        if (minPrice != null && property.getPrice().compareTo(minPrice) < 0) return false;
        if (maxPrice != null && property.getPrice().compareTo(maxPrice) > 0) return false;
        return true;
    }
}

package com.estateelite.pattern.strategy;

import com.estateelite.model.Property;

public class BedroomFilterStrategy implements SearchFilterStrategy {

    private final Integer minBedrooms;

    public BedroomFilterStrategy(Integer minBedrooms) {
        this.minBedrooms = minBedrooms;
    }

    @Override
    public boolean matches(Property property) {
        if (minBedrooms == null) return true;
        return property.getBedrooms() != null && property.getBedrooms() >= minBedrooms;
    }
}

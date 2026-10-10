package com.estateelite.pattern.strategy;

import com.estateelite.model.Property;

public class LocationFilterStrategy implements SearchFilterStrategy {

    private final String location;

    public LocationFilterStrategy(String location) {
        this.location = location;
    }

    @Override
    public boolean matches(Property property) {
        if (location == null || location.isBlank()) return true;
        return property.getLocation() != null &&
                property.getLocation().toLowerCase().contains(location.toLowerCase());
    }
}

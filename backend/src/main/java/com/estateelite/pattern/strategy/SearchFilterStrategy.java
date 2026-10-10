package com.estateelite.pattern.strategy;

import com.estateelite.model.Property;

public interface SearchFilterStrategy {
    boolean matches(Property property);
}

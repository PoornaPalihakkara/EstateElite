package com.estateelite.pattern.strategy;

import com.estateelite.model.Property;

import java.util.ArrayList;
import java.util.List;

public class SearchContext {

    private final List<SearchFilterStrategy> strategies = new ArrayList<>();

    public SearchContext addStrategy(SearchFilterStrategy strategy) {
        if (strategy != null) {
            strategies.add(strategy);
        }
        return this;
    }

    public List<Property> apply(List<Property> candidates) {
        List<Property> result = new ArrayList<>();
        for (Property property : candidates) {
            boolean matchesAll = true;
            for (SearchFilterStrategy strategy : strategies) {
                if (!strategy.matches(property)) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) result.add(property);
        }
        return result;
    }
}

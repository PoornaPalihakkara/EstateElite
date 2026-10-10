package com.estateelite.service;

import com.estateelite.dto.response.PropertyResponse;
import com.estateelite.model.Property;
import com.estateelite.pattern.strategy.BedroomFilterStrategy;
import com.estateelite.pattern.strategy.LocationFilterStrategy;
import com.estateelite.pattern.strategy.PriceRangeFilterStrategy;
import com.estateelite.pattern.strategy.SearchContext;
import com.estateelite.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

//Buyer Search & Wishlist Module (search half).
//Builds up a SearchContext (Strategy Pattern) from whichever query
//parameters the buyer supplied and applies it against the property catalogue.

@Service
@RequiredArgsConstructor
public class PropertySearchService {

    private final PropertyRepository propertyRepository;

    @Transactional(readOnly = true)
    public List<PropertyResponse> search(String location, BigDecimal minPrice, BigDecimal maxPrice, Integer minBedrooms) {
        List<Property> all = propertyRepository.findAll();

        SearchContext context = new SearchContext()
                .addStrategy(new LocationFilterStrategy(location))
                .addStrategy(new PriceRangeFilterStrategy(minPrice, maxPrice))
                .addStrategy(new BedroomFilterStrategy(minBedrooms));

        return context.apply(all).stream().map(PropertyResponse::from).collect(Collectors.toList());
    }
}

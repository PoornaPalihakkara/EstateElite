package com.estateelite.pattern.builder;

import com.estateelite.model.Property;
import com.estateelite.model.PropertyStatus;
import com.estateelite.model.User;

import java.math.BigDecimal;

public class PropertyBuilderDirector {

    public static Property buildNewListing(String title, String description, BigDecimal price,
                                             String location, Integer bedrooms, Integer bathrooms,
                                             Integer areaSqft, Integer parkingSpaces,
                                             Boolean poolAvailable, User seller) {
        return Property.builder()
                .title(title)
                .description(description)
                .price(price)
                .location(location)
                .bedrooms(bedrooms)
                .bathrooms(bathrooms)
                .areaSqft(areaSqft)
                .parkingSpaces(parkingSpaces != null ? parkingSpaces : 0)
                .poolAvailable(poolAvailable != null ? poolAvailable : false)
                .seller(seller)
                .status(PropertyStatus.AVAILABLE)
                .viewCount(0)
                .build();
    }
}

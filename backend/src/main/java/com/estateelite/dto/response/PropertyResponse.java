package com.estateelite.dto.response;

import com.estateelite.model.Property;
import com.estateelite.model.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String location;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer areaSqft;
    private Integer parkingSpaces;
    private Boolean poolAvailable;
    private PropertyStatus status;
    private Integer viewCount;
    private Long sellerId;
    private String sellerName;
    private List<PropertyImageResponse> images;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PropertyResponse from(Property p) {
        return PropertyResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .price(p.getPrice())
                .location(p.getLocation())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .areaSqft(p.getAreaSqft())
                .parkingSpaces(p.getParkingSpaces())
                .poolAvailable(p.getPoolAvailable())
                .status(p.getStatus())
                .viewCount(p.getViewCount())
                .sellerId(p.getSeller().getId())
                .sellerName(p.getSeller().getFullName())
                .images(p.getImages().stream().map(PropertyImageResponse::from).collect(Collectors.toList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

package com.estateelite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull @Positive
    private BigDecimal price;
    @NotBlank
    private String location;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer areaSqft;

    private Integer parkingSpaces;

    private Boolean poolAvailable;
}


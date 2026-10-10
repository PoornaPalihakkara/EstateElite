package com.estateelite.dto.request;

import com.estateelite.model.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WishlistRequest {
    @NotNull
    private Long propertyId;
    private String note;
    private Priority priority;
    private BigDecimal targetPrice;
}

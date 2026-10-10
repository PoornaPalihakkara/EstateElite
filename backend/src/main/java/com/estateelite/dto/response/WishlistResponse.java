package com.estateelite.dto.response;

import com.estateelite.model.Priority;
import com.estateelite.model.WishlistItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private Long id;
    private PropertyResponse property;
    private String note;
    private Priority priority;
    private BigDecimal targetPrice;
    private LocalDateTime addedAt;

    public static WishlistResponse from(WishlistItem w) {
        return WishlistResponse.builder()
                .id(w.getId())
                .property(PropertyResponse.from(w.getProperty()))
                .note(w.getNote())
                .priority(w.getPriority())
                .targetPrice(w.getTargetPrice())
                .addedAt(w.getAddedAt())
                .build();
    }
}

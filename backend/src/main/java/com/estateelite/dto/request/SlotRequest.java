package com.estateelite.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SlotRequest {
    @NotNull
    private LocalDateTime slotDateTime;
    private Long propertyId;
}

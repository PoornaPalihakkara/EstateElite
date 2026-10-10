package com.estateelite.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequest {
    @NotNull
    private Long propertyId;
    @NotNull
    private Long slotId;
}

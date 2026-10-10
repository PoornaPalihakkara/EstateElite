package com.estateelite.dto.request;

import com.estateelite.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentStatusUpdateRequest {
    @NotNull
    private AppointmentStatus status;
    // optional: only used when rescheduling
    private LocalDateTime newSlotDateTime;
}

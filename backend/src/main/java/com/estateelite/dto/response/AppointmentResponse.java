package com.estateelite.dto.response;

import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private java.math.BigDecimal propertyPrice;
    private Long agentId;
    private String agentName;
    private Long buyerId;
    private String buyerName;
    private LocalDateTime slotDateTime;
    private AppointmentStatus status;
    private LocalDateTime createdAt;

    public static AppointmentResponse from(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .propertyId(a.getProperty().getId())
                .propertyTitle(a.getProperty().getTitle())
                .propertyPrice(a.getProperty().getPrice())
                .agentId(a.getAgent().getId())
                .agentName(a.getAgent().getFullName())
                .buyerId(a.getBuyer().getId())
                .buyerName(a.getBuyer().getFullName())
                .slotDateTime(a.getSlotDateTime())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}

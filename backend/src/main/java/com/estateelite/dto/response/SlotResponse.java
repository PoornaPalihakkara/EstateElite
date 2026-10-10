package com.estateelite.dto.response;

import com.estateelite.model.AvailabilitySlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {
    private Long id;
    private Long agentId;
    private String agentName;
    private Long propertyId;
    private String propertyTitle;
    private LocalDateTime slotDateTime;
    private Boolean isBooked;

    public static SlotResponse from(AvailabilitySlot s) {
        return SlotResponse.builder()
                .id(s.getId())
                .agentId(s.getAgent().getId())
                .agentName(s.getAgent().getFullName())
                .propertyId(s.getProperty() != null ? s.getProperty().getId() : null)
                .propertyTitle(s.getProperty() != null ? s.getProperty().getTitle() : null)
                .slotDateTime(s.getSlotDateTime())
                .isBooked(s.getIsBooked())
                .build();
    }
}

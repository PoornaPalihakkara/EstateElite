package com.estateelite.dto.request;

import com.estateelite.model.PropertyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyStatusUpdateRequest {
    @NotNull
    private PropertyStatus status;
}

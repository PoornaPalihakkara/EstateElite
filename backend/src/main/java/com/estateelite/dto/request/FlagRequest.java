package com.estateelite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FlagRequest {

    @NotBlank(message = "Please provide a reason / note")
    @Size(min = 5, max = 1000, message = "Note must be between 5 and 1000 characters")
    private String note;
}

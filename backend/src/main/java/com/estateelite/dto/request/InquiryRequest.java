package com.estateelite.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InquiryRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private Long agencyId; // null = goes to Admin
    @NotBlank
    private String message;
}

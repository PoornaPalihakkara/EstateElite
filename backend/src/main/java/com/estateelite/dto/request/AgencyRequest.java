package com.estateelite.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgencyRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String licenseNumber;
    private String contactEmail;
    private String contactPhone;
    private String address;
}

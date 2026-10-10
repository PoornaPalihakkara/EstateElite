package com.estateelite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservePropertyRequest {

    @NotNull(message = "Property id is required")
    private Long propertyId;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Cardholder name is required")
    @Size(min = 2, max = 80)
    private String cardholderName;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Expiry is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiry must be MM/YY")
    private String expiry;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;
}

package com.estateelite.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    private Long propertyId;
    @NotNull
    private Long buyerId;
    @NotNull @Positive
    private BigDecimal amount;
}

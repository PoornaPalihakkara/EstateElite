package com.estateelite.dto.request;

import com.estateelite.model.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionStatusUpdateRequest {
    @NotNull
    private TransactionStatus status;
}

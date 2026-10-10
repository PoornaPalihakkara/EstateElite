package com.estateelite.dto.response;

import com.estateelite.model.PaymentMethod;
import com.estateelite.model.Transaction;
import com.estateelite.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String invoiceNumber;
    private Long propertyId;
    private String propertyTitle;
    private Long buyerId;
    private String buyerName;
    private String financeOfficerName;
    private BigDecimal amount;
    private TransactionStatus status;
    private PaymentMethod paymentMethod;
    private String receiptUrl;
    private String transferReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransactionResponse from(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .invoiceNumber(t.getInvoiceNumber())
                .propertyId(t.getProperty().getId())
                .propertyTitle(t.getProperty().getTitle())
                .buyerId(t.getBuyer().getId())
                .buyerName(t.getBuyer().getFullName())
                .financeOfficerName(t.getFinanceOfficer() != null ? t.getFinanceOfficer().getFullName() : null)
                .amount(t.getAmount())
                .status(t.getStatus())
                .paymentMethod(t.getPaymentMethod())
                .receiptUrl(t.getReceiptUrl())
                .transferReference(t.getTransferReference())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}

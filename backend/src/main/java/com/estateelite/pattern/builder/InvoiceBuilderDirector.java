package com.estateelite.pattern.builder;

import com.estateelite.model.Property;
import com.estateelite.model.Transaction;
import com.estateelite.model.TransactionStatus;
import com.estateelite.model.User;

import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

public class InvoiceBuilderDirector {

    public static Transaction buildDraftInvoice(Property property, User buyer, BigDecimal amount) {
        String invoiceNumber = "EE-" + Year.now().getValue() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return Transaction.builder()
                .invoiceNumber(invoiceNumber)
                .property(property)
                .buyer(buyer)
                .amount(amount)
                .status(TransactionStatus.DRAFT)
                .build();
    }
}

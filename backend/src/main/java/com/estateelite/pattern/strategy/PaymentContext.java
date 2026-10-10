package com.estateelite.pattern.strategy;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PaymentContext {

    //Card payment fields (demo gateway)
    private String cardholderName;
    private String cardNumber;
    private String expiry;   // MM/YY
    private String cvv;

    //Bank transfer fields
    //Photo of the bank transfer receipt (required for BANK_TRANSFER)
    private MultipartFile receiptFile;

    //Optional reference / transaction ID the buyer wrote on the slip.
    private String transferReference;
}

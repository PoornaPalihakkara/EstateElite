package com.estateelite.pattern.strategy;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.PaymentMethod;
import com.estateelite.model.Transaction;
import com.estateelite.model.TransactionStatus;
import com.estateelite.model.User;
import com.estateelite.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class BankTransferPaymentStrategy implements PaymentStrategy {

    private final FileStorageService fileStorageService;

    @Override
    public void process(Transaction transaction, User buyer, PaymentContext context) {
        MultipartFile receipt = context.getReceiptFile();
        if (receipt == null || receipt.isEmpty()) {
            throw new BadRequestException("Bank transfer requires a photo of the transfer receipt");
        }

        String receiptUrl = fileStorageService.store(receipt);

        transaction.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        transaction.setReceiptUrl(receiptUrl);
        transaction.setTransferReference(
                context.getTransferReference() != null && !context.getTransferReference().isBlank()
                        ? context.getTransferReference().trim()
                        : null);
        transaction.setStatus(TransactionStatus.PENDING);
    }

    @Override
    public String getMethodName() {
        return "BANK_TRANSFER";
    }
}

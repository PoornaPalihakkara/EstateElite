package com.estateelite.pattern.strategy;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.PaymentMethod;
import com.estateelite.model.Transaction;
import com.estateelite.model.TransactionStatus;
import com.estateelite.model.User;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public void process(Transaction transaction, User buyer, PaymentContext context) {
        String digits = context.getCardNumber() == null ? "" : context.getCardNumber().replaceAll("\\D", "");
        if (digits.length() != 16) {
            throw new BadRequestException("Card number must be exactly 16 digits");
        }

        String expiry = context.getExpiry() == null ? "" : context.getExpiry().trim();
        if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            throw new BadRequestException("Expiry must be MM/YY");
        }
        int expMonth = Integer.parseInt(expiry.substring(0, 2));
        int expYear = 2000 + Integer.parseInt(expiry.substring(3, 5));
        YearMonth cardExpiry = YearMonth.of(expYear, expMonth);
        if (cardExpiry.isBefore(YearMonth.now())) {
            throw new BadRequestException("Card has expired — enter a valid future expiry date");
        }

        if (context.getCvv() == null || !context.getCvv().matches("^\\d{3,4}$")) {
            throw new BadRequestException("CVV must be 3 or 4 digits");
        }

        // Demo decline path
        if (digits.startsWith("4000")) {
            throw new BadRequestException("Demo payment declined — use card 4242 4242 4242 4242 to succeed");
        }

        transaction.setPaymentMethod(PaymentMethod.CARD);
        transaction.setStatus(TransactionStatus.COMPLETED);
        // No receipt for card payments
        transaction.setReceiptUrl(null);
        transaction.setTransferReference(null);
    }

    @Override
    public String getMethodName() {
        return "CARD";
    }
}

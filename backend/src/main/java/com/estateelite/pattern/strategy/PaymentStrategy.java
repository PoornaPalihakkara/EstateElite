package com.estateelite.pattern.strategy;

import com.estateelite.model.Transaction;
import com.estateelite.model.User;

public interface PaymentStrategy {

    void process(Transaction transaction, User buyer, PaymentContext context);

    String getMethodName();
}

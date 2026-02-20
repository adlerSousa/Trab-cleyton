package com.delivery.Impl;

import com.delivery.OrderEvent;
import com.delivery.PaymentStrategy;

public class Payment implements PaymentStrategy {
    private static final double LIMIT_AMOUNT = 100.0;

    @Override
    public boolean processPayment(OrderEvent event) {
        return event.getAmount() <= LIMIT_AMOUNT;
    }
}
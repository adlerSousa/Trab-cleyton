package com.delivery;

public interface PaymentStrategy {
    boolean processPayment(OrderEvent event);
}
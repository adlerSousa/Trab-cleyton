package com.delivery;

public class OrderEvent {
    private String orderId;
    private String customerName;
    private String restaurant;
    private double amount;

    // Construtor vazio (obrigatório para o Jackson/JSON)
    public OrderEvent() {}

    // Construtor completo
    public OrderEvent(String orderId, String customerName, String restaurant, double amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.restaurant = restaurant;
        this.amount = amount;
    }

    // Getters e Setters (importante ter os Setters para o Consumer ler o JSON)
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getRestaurant() { return restaurant; }
    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

package com.delivery.service;

import com.delivery.OrderEvent;

public class DeliveryService {
    public void despacharPedido(OrderEvent event) {
    	event.setStatus(OrderEvent.StatusPedido.SAIU_PARA_ENTREGA);

        System.out.println("\n--- MOTOBOY ACIONADO ---");
        System.out.println("Pedido: " + event.getOrderId());
        System.out.println("Cliente: " + event.getCustomerName());
        System.out.println("Restaurante: " + event.getRestaurant());
        System.out.println("Status Final: " + event.getStatus()); 
        System.out.println("Entregador a caminho...");
    }
}
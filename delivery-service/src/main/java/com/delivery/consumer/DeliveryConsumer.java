package com.delivery.consumer;

import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.delivery.OrderEvent;
import com.delivery.factory.KafkaConfigFactory;
import com.delivery.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryConsumer {
    public static void main(String[] args) throws Exception {
        DeliveryService deliveryService = new DeliveryService();
        ObjectMapper mapper = new ObjectMapper();
        
        // Chamada limpa da Factory
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConfigFactory.getConsumerProperties("delivery-group")); 
        consumer.subscribe(Collections.singletonList("pedido-pago"));

        System.out.println("Serviço de entrega aguardando pedidos pagos...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                OrderEvent event = mapper.readValue(record.value(), OrderEvent.class);
                deliveryService.despacharPedido(event);
            }
        }
    }
}
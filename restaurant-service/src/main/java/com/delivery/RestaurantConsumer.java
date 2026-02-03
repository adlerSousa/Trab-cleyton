package com.delivery;

import java.util.Collections;
import java.util.Properties;
import java.time.Duration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import com.delivery.OrderEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestaurantConsumer {

    private static final String TOPIC = "order-created";

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "restaurant-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("🍽️ Restaurante aguardando pedidos...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                OrderEvent event = mapper.readValue(record.value(), OrderEvent.class);

                System.out.println("📦 Pedido recebido:");
                System.out.println("ID: " + event.getOrderId());
                System.out.println("Cliente: " + event.getCustomerName());
                System.out.println("Restaurante: " + event.getRestaurant());
                System.out.println("💡 Preparando pedido...\n");
            }
        }
    }

}

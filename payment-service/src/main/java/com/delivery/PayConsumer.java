package com.delivery;

import java.util.Collections;
import java.util.Properties;
import java.time.Duration;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PayConsumer {
    public static void main(String[] args) throws Exception {

        System.out.println("💳 Serviço de pagamento aguardando pedidos aprovados...");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("pedido-aprovado"));

        ObjectMapper mapper = new ObjectMapper();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                OrderEvent event = mapper.readValue(record.value(), OrderEvent.class);

                System.out.println("\n💰 Processando pagamento do pedido:");
                System.out.println("ID: " + event.getOrderId());
                System.out.println("Cliente: " + event.getCustomerName());
                System.out.println("Restaurante: " + event.getRestaurant());
                System.out.println("Valor: R$ " + event.getAmount());
                System.out.println("💸 Pagamento aprovado!");
            }

        }
    }
}

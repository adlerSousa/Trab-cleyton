package com.delivery;

import java.util.Arrays;
import java.time.Duration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.delivery.factory.KafkaConfigFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestaurantConsumer {

    public static void main(String[] args) throws Exception {

        // 1. Usa a Factory para pegar as configurações limpas!
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConfigFactory.getConsumerProperties("restaurant-group"));
        consumer.subscribe(Arrays.asList("order-created", "pedido-cancelado"));

        KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfigFactory.getProducerProperties());

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Restaurante operando e aguardando eventos...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                OrderEvent event = mapper.readValue(record.value(), OrderEvent.class);

                if (record.topic().equals("order-created")) {
                    System.out.println("Pedido [" + event.getOrderId() + "] recebido. Iniciando preparo...");
                    
                    // CORREÇÃO: Usando o Enum corretamente
                    event.setStatus(OrderEvent.StatusPedido.PREPARANDO);
                    
                    String json = mapper.writeValueAsString(event);
                    producer.send(new ProducerRecord<>("pedido-aprovado", event.getOrderId(), json));
                    System.out.println("Pedido aprovado! Enviado para o setor de pagamento.");

                } else if (record.topic().equals("pedido-cancelado")) {
                    System.err.println("ALERTA DE CANCELAMENTO: Pedido [" + event.getOrderId() + "] cancelado.");
                }
            }
        }
    }
}
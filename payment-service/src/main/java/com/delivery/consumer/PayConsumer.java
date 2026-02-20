package com.delivery.consumer;

import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.delivery.OrderEvent;
import com.delivery.PaymentStrategy;
import com.delivery.Impl.Payment;
import com.delivery.factory.KafkaConfigFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PayConsumer {
    public static void main(String[] args) throws Exception {
        System.out.println("Serviço de pagamento aguardando pedidos...");

        // 1. Usando a Factory Centralizada (S.R.P. e D.R.Y.)
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConfigFactory.getConsumerProperties("payment-group"));
        consumer.subscribe(Collections.singletonList("pedido-aprovado"));

        KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfigFactory.getProducerProperties());
        ObjectMapper mapper = new ObjectMapper();
        
        // 2. Strategy Pattern
        PaymentStrategy paymentStrategy = new Payment();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                OrderEvent event = mapper.readValue(record.value(), OrderEvent.class);
                boolean isApproved = paymentStrategy.processPayment(event);

                if (isApproved) {
                    System.out.println("Pagamento aprovado para o pedido " + event.getOrderId());
                    event.setStatus(OrderEvent.StatusPedido.PAGO);
                    enviarEvento(producer, mapper, "pedido-pago", event);
                } else {
                    System.out.println("Pagamento recusado para o pedido " + event.getOrderId());
                    event.setStatus(OrderEvent.StatusPedido.CANCELADO);
                    enviarEvento(producer, mapper, "pedido-cancelado", event);
                }
            }
        }
    }

    private static void enviarEvento(KafkaProducer<String, String> producer, ObjectMapper mapper, String topic, OrderEvent event) throws Exception {
        String jsonAtt = mapper.writeValueAsString(event);
        producer.send(new ProducerRecord<>(topic, event.getOrderId(), jsonAtt));
    }
}
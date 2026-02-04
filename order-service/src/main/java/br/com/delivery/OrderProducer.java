package br.com.delivery;

import java.util.Properties;
import com.delivery.OrderEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderProducer {
    private static final String TOPIC = "order-created";

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        ObjectMapper mapper = new ObjectMapper();

        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                "Ayler",
                "PK pizzaria",
                79.90
        );

        String json = mapper.writeValueAsString(event);

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, event.getOrderId(), json);

        producer.send(record);
        producer.close();

        System.out.println("Pedido publicado no Kafka!");
    }
}

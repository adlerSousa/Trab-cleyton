package br.com.delivery;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import com.delivery.OrderEvent;
import com.delivery.factory.KafkaConfigFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PreDestroy;

@Component
public class OrderProducer {

    private final KafkaProducer<String, String> producer;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String TOPIC = "order-created";

    public OrderProducer() {
        // Factory garantindo padronização
        this.producer = new KafkaProducer<>(KafkaConfigFactory.getProducerProperties());
    }

    public void enviarPedidoCriado(OrderEvent pedido) throws Exception {
        String json = mapper.writeValueAsString(pedido);
        producer.send(new ProducerRecord<>(TOPIC, pedido.getOrderId(), json));
        System.out.println("Producer: Pedido [" + pedido.getOrderId() + "] publicado com sucesso no tópico " + TOPIC);
    }

    @PreDestroy
    public void fecharConexao() {
        if (this.producer != null) {
            this.producer.close();
        }
    }
}
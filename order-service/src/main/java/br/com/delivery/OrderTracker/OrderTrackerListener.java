package br.com.delivery.OrderTracker;

import java.time.Duration;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import com.delivery.OrderEvent;
import com.delivery.factory.KafkaConfigFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.delivery.service.OrderService;
import jakarta.annotation.PostConstruct;

@Component
public class OrderTrackerListener {

    private final OrderService orderService;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderTrackerListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostConstruct
    public void iniciarRastreador() {
        Thread trackerThread = new Thread(() -> {
            // Reutilizando a configuração da Factory central
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConfigFactory.getConsumerProperties("tracker-group"));
            consumer.subscribe(Arrays.asList("pedido-aprovado", "pedido-pago", "pedido-cancelado", "SAIU_PARA_ENTREGA"));

            System.out.println("Rastreador de pedidos iniciado em background...");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        OrderEvent eventoAtualizado = mapper.readValue(record.value(), OrderEvent.class);
                        orderService.atualizarStatus(eventoAtualizado.getOrderId(), eventoAtualizado.getStatus());
                        System.out.println("Tracker: Status do pedido " + eventoAtualizado.getOrderId() + " atualizado para " + eventoAtualizado.getStatus());
                    } catch (Exception e) {
                        System.err.println("Erro ao rastrear evento: " + e.getMessage());
                    }
                }
            }
        });
        
        trackerThread.setDaemon(true);
        trackerThread.start();
    }
}
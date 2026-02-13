package br.com.delivery;

import com.delivery.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para criação e acompanhamento de pedidos via Kafka")
public class OrderController {

    private final KafkaProducer<String, String> producer;
    private final ObjectMapper mapper = new ObjectMapper();
    
    private final Map<String, String> baseDeDadosPedidos = new ConcurrentHashMap<>();

    public OrderController() {
        Properties propsProd = new Properties();
        propsProd.put("bootstrap.servers", "localhost:9092");
        propsProd.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        propsProd.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(propsProd);

        iniciarRastreadorKafka();
    }

    @PostMapping
    @Operation(
        summary = "Cria um novo pedido", 
        description = "Gera um UUID para o pedido, salva na memória local com status 'CRIADO' e publica no tópico 'order-created' do Kafka."
    )
    public OrderEvent criarPedido(@RequestBody OrderEvent pedido) throws Exception {
        if (pedido.getOrderId() == null) pedido.setOrderId(UUID.randomUUID().toString());
        
        pedido.setStatus("CRIADO");
        baseDeDadosPedidos.put(pedido.getOrderId(), pedido.getStatus());
        
        String json = mapper.writeValueAsString(pedido);
        ProducerRecord<String, String> record = new ProducerRecord<>("order-created", pedido.getOrderId(), json);
        producer.send(record);

        return pedido;
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Consulta o status de um pedido", 
        description = "Busca na base de dados em memória o status atualizado do pedido. O status pode mudar conforme os microserviços processam os eventos."
    )
    public String consultarStatus(@PathVariable String id) {
        return "Status do Pedido (" + id + "): " + baseDeDadosPedidos.getOrDefault(id, "NÃO ENCONTRADO");
    }

    private void iniciarRastreadorKafka() {
        new Thread(() -> {
            Properties propsCons = new Properties();
            propsCons.put("bootstrap.servers", "localhost:9092");
            propsCons.put("group.id", "tracker-group"); 
            propsCons.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            propsCons.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(propsCons);
            consumer.subscribe(Arrays.asList("pedido-aprovado", "pedido-pago", "pedido-cancelado"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        OrderEvent eventoAtualizado = mapper.readValue(record.value(), OrderEvent.class);
                        baseDeDadosPedidos.put(eventoAtualizado.getOrderId(), eventoAtualizado.getStatus());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
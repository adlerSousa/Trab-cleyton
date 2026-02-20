package br.com.delivery.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.delivery.OrderEvent;

import br.com.delivery.OrderProducer;

@Service
public class OrderService {

    private final OrderProducer orderProducer;
    // Base de dados isolada no Service
    private final Map<String, OrderEvent.StatusPedido> baseDeDadosPedidos = new ConcurrentHashMap<>();

    // Injeção de Dependência (DIP)
    public OrderService(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    public OrderEvent processarNovoPedido(OrderEvent pedido) throws Exception {
        if (pedido.getOrderId() == null) {
            pedido.setOrderId(UUID.randomUUID().toString());
        }
        
        pedido.setStatus(OrderEvent.StatusPedido.CRIADO);
        atualizarStatus(pedido.getOrderId(), pedido.getStatus());
        
        // Delega o envio para a camada de mensageria
        orderProducer.enviarPedidoCriado(pedido);

        return pedido;
    }

    public void atualizarStatus(String orderId, OrderEvent.StatusPedido status) {
        if (orderId != null && status != null) {
            baseDeDadosPedidos.put(orderId, status);
        }
    }

    public String consultarStatus(String id) {
        OrderEvent.StatusPedido status = baseDeDadosPedidos.get(id);
        return "Status do Pedido (" + id + "): " + (status != null ? status.name() : "NÃO ENCONTRADO");
    }

	
}
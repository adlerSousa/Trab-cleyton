package br.com.delivery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delivery.OrderEvent;

import br.com.delivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para criação e acompanhamento de pedidos via Kafka")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo pedido", description = "Gera um UUID para o pedido, salva na memória local e publica no tópico 'order-created' do Kafka.")
    public OrderEvent criarPedido(@RequestBody OrderEvent pedido) throws Exception {
        return orderService.processarNovoPedido(pedido);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta o status de um pedido", description = "Busca na base de dados em memória o status atualizado do pedido.")
    public String consultarStatus(@PathVariable String id) {
        return orderService.consultarStatus(id);
    }
}
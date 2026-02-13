# Delivery System - Kafka Pub/Sub Architecture

Este projeto implementa uma Coreografia de Microserviços baseada na arquitetura Publish-Subscriber utilizando Apache Kafka. O sistema simula o fluxo completo de um pedido de delivery, desde a criação até a entrega final, garantindo o desacoplamento total entre os serviços.

**Trabalho Final - Projeto de Sistemas de Software (2025-2)**  
Tema 4: Arquitetura Publish–subscriber com exemplo usando Apache Kafka.

## Arquitetura do Sistema

A comunicação entre os serviços é puramente assíncrona. Nenhum serviço conhece o endpoint do outro; eles apenas reagem a eventos publicados em tópicos específicos do Kafka.

### Fluxo de Eventos:
- **order-created**: Publicado pelo order-service ao realizar um pedido.
- **pedido-aprovado**: Publicado pelo restaurant-service após simular o preparo.
- **pedido-pago**: Publicado pelo payment-service após confirmar a transação.
- **Finalização**: O delivery-service consome o evento de pagamento e despacha o entregador.

## Tecnologias Utilizadas
- **Linguagem**: Java 20 (Record types, Modern Switch, etc.)
- **Gerenciamento de Dependências**: Apache Maven
- **Mensageria**: Apache Kafka (executando via Docker)
- **Serialização**: Jackson (JSON)

## Como Executar

1. **Clonar e Acessar o Projeto**
    ```bash
    git clone https://github.com/SeuUsuario/delivery-pubsub-kafka.git
    cd delivery-pubsub-kafka
    ```

2. **Subir a Infraestrutura (Docker)**
   O projeto inclui um `docker-compose.yml` com as imagens da Confluent para o Kafka e Zookeeper.
    ```bash
    docker-compose up -d
    ```
   Aguarde cerca de 15 segundos para o broker inicializar completamente.

3. **Instalar o Módulo Comum**
   Este passo é obrigatório, pois todos os microserviços dependem do objeto `OrderEvent` definido aqui.
    ```bash
    cd delivery-common
    mvn clean install
    cd ..
    ```

4. **Executar os Serviços (Subscribers)**
   Abra terminais diferentes para cada comando abaixo para visualizar os logs de cada serviço:

   **Restaurante:**
    ```bash
    cd restaurant-service && mvn exec:java -Dexec.mainClass="com.delivery.RestaurantConsumer"
    ```

   **Pagamento:**
    ```bash
    cd payment-service && mvn exec:java -Dexec.mainClass="com.delivery.PayConsumer"
    ```

   **Entrega:**
    ```bash
    cd delivery-service && mvn exec:java -Dexec.mainClass="com.delivery.DeliveryConsumer"
    ```

5. **Simular um Pedido (Publisher)**
   Com os consumidores rodando, execute o produtor para iniciar a coreografia:
    ```bash
    cd order-service && mvn exec:java -Dexec.mainClass="br.com.delivery.OrderProducer"
    ```

## Estrutura de Pastas
```text
├── delivery-common/      # Classe OrderEvent compartilhada
├── delivery-service/     # Consome 'pedido-pago'
├── order-service/        # Produz 'order-created' (Ponto de entrada)
├── payment-service/      # Consome 'pedido-aprovado' e produz 'pedido-pago'
├── restaurant-service/   # Consome 'order-created' e produz 'pedido-aprovado'
└── docker-compose.yml    # Infraestrutura do Kafka
```


## Decisões Técnicas & S.O.L.I.D
Single Responsibility Principle: Cada microserviço é responsável por apenas uma etapa do domínio de negócio (Pedido, Cozinha, Financeiro, Logística).

Dependency Inversion: Os serviços dependem da abstração do evento no Kafka, e não de implementações concretas de outros serviços.

Resiliência: Se o delivery-service estiver offline, as mensagens de pagamento aprovado ficam retidas no Kafka e são processadas automaticamente assim que o serviço retornar.

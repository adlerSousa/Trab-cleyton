# delivery-pubsub-kafka

Projeto: Sistema de Pedidos com Kafka (Arquitetura de Eventos)

Este projeto simula o fluxo de um aplicativo de delivery usando microsserviços e Apache Kafka para comunicação assíncrona entre serviços.

Objetivo

Demonstrar:

Comunicação entre microsserviços

Arquitetura orientada a eventos

Uso do Kafka como mensageria

Desacoplamento entre serviços

Arquitetura do Sistema

Fluxo do pedido:

Order Service  →  Restaurant Service  →  Payment Service  →  Delivery Service
   (cria)           (aprova)              (paga)              (entrega)


Cada etapa publica um evento no Kafka.

Serviços Criados
Serviço	Função	Tópico que ESCUTA	Tópico que PUBLICA
order-service	Cria o pedido	—	pedido-criado
restaurant-service	Aprova pedido	pedido-criado	pedido-aprovado
payment-service	Processa pagamento	pedido-aprovado	pedido-pago
delivery-service	Realiza entrega	pedido-pago	—
Estrutura dos Projetos

Todos são projetos Maven:

delivery-pubsub-kafka/
│
├── delivery-common      → Classe compartilhada OrderEvent
├── order-service        → Produtor inicial
├── restaurant-service   → Consumer + Produtor
├── payment-service      → Consumer + Produtor
└── delivery-service     → Consumer final

Classe Compartilhada

Arquivo comum para todos os serviços:

delivery-common → OrderEvent.java

Contém os dados do pedido:

ID

Cliente

Restaurante

Valor

Evita duplicação de código.

⚙️ Pré-requisitos

Java 17+

Maven

Apache Kafka rodando

Zookeeper rodando

Como Iniciar o Sistema

A ordem é MUITO IMPORTANTE

1 — Restaurant Service
cd restaurant-service
mvn compile exec:java "-Dexec.mainClass=com.delivery.RestaurantConsumer"

2 — Payment Service
cd payment-service
mvn compile exec:java "-Dexec.mainClass=com.delivery.PayConsumer"

3 — Delivery Service
cd delivery-service
mvn compile exec:java "-Dexec.mainClass=com.delivery.DeliveryConsumer"

4 — Order Service (Dispara o fluxo)
cd order-service
mvn clean compile exec:java "-Dexec.mainClass=br.com.delivery.OrderProducer"

- O Que Acontece Quando Roda

Um pedido percorre todo o sistema:

Order Service cria o pedido

Restaurant recebe e aprova

Payment processa pagamento

Delivery envia para entrega

- Conceitos Demonstrados

- Arquitetura orientada a eventos
- Comunicação assíncrona
- Pub/Sub com Kafka
- Desacoplamento de serviços
- Serialização JSON
- Compartilhamento de modelo via módulo comum

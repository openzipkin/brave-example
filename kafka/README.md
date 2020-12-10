## Tracing Example: Kafka 

Instead of client/server, this example implements a producer->streams->consumer flow.

*   brave.example.ProducerService, ProcessorService and ConsumerService : Kafka Clients and Streams applications.
*   brave.example.MessagingTracingFactory : Configures the tracing subsystem

### Docker Compose

```shell
docker-compose -f docker-compose.yml -f kafka/docker-compose.yml up -d
```
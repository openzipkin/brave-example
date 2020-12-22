## Tracing Example: Kafka 

Instead of client/server, this example implements a http->producer->streams->consumer flow.

*   brave.example.Frontend : HTTP endpoint and Kafka Producer application.
*   brave.example.Backend : Kafka Consumer and Streams applications.
*   brave.example.TracingFactory : Configures the tracing subsystem

### Docker Compose

```shell
cd armeria-kafka/
docker-compose up -d
```
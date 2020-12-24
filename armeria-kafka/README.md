## Tracing Example: Armeria/Kafka Clients and Streams/Java 15

Instead of client/server, this example implements a http->producer->streams->consumer flow.

* [brave.example.Frontend](src/main/java/brave/example/Frontend.java) - HTTP endpoint and Kafka Producer application
* [brave.example.Backend](src/main/java/brave/example/Backend.java) - Kafka Consumer and Streams applications
* [brave.example.TracingConfiguration](src/main/java/brave/example/TracingConfiguration.java) - Configures trace instrumentation

Here's an example screen shot:
![screen shot](https://user-images.githubusercontent.com/64215/103084964-4fb11a00-461b-11eb-9035-84d0c1aa00d6.png)

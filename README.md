# Basic example showing distributed tracing across Java applications
This is an example app where two Java services collaborate on a request.

Notably, these services send data to [Zipkin](https://zipkin.io/), a
distributed tracing system. Zipkin allows you to see the how long the operation
took, as well how much time was spent in each service.

Here's an example of what it looks like:

<img width="979" alt="zipkin screen shot" src="https://user-images.githubusercontent.com/64215/95572045-f98cfb80-0a5b-11eb-9d3b-9a1b9b1db6b4.png">

# Implementation Overview
This example has two services: frontend and backend. Both are [instrumented](https://github.com/openzipkin/brave/tree/master/instrumentation)
to send tracing data to a third service [Zipkin](https://zipkin.io/). [Brave](https://github.com/openzipkin/brave)
performs this function.

# Running the example
To setup the demo, you need to start Frontend, Backend and Zipkin. You can do
this using Java commands or Docker.

Once the services start, open http://localhost:8081/
* This calls the backend (http://127.0.0.1:9000/api) and shows its result: a formatted date.

Afterwards, you can view traces that went through the backend via http://127.0.0.1:9411/zipkin?serviceName=backend
* This is a locally run zipkin service which keeps traces in memory

## Tips

There are some interesting details that apply to all examples:
* If you pass the header `user_name` Brave will automatically propagate it to the backend!
  * `curl -s localhost:8081 -H'user_name: JC'`
* The below Logback pattern adds trace and span identifiers into log output
  * `%d{HH:mm:ss.SSS} [%thread] [%X{userName}] [%X{traceId}/%X{spanId}] %-5level %logger{36} - %msg%n`

## Example projects

Here are the example projects you can try:

* [armeria](armeria) `BRAVE_EXAMPLE=armeria docker-compose up`
  * Runtime: Armeria, SLF4J 1.7, JRE 15
  * Trace Instrumentation: [Armeria](https://armeria.dev/), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Brave API](https://github.com/openzipkin/brave/tree/master/brave#setup) [Java](armeria/src/main/java/brave/example/HttpTracingFactory.java)

* [armeria-kafka](armeria-kafka) `BRAVE_EXAMPLE=armeria-kafka docker-compose -f docker-compose.yml -f docker-compose-kafka.yml up`
  * Runtime: Armeria, Kafka Clients and Streams 2.7, SLF4J 1.7, JRE 15
  * Trace Instrumentation: [Armeria](https://armeria.dev/), [Kafka Clients](https://github.com/openzipkin/brave/tree/master/instrumentation/kafka-clients), [Kafka Streams](https://github.com/openzipkin/brave/tree/master/instrumentation/kafka-streams), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Brave API](https://github.com/openzipkin/brave/tree/master/brave#setup) [Java](armeria/src/main/java/brave/example/HttpTracingFactory.java)

* [dropwizard](dropwizard) `BRAVE_EXAMPLE=dropwizard docker-compose up`
  * Runtime: JaxRS 2, Jersey 2.31, Apache HttpClient 4.5, Jetty 9.4, SLF4J 1.7, JRE 15
  * Trace Instrumentation: [Jersey Server](https://github.com/openzipkin/brave/tree/master/instrumentation/jersey-server), [Apache HttpClient](https://github.com/openzipkin/brave/tree/master/instrumentation/httpclient), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Dropwizard Zipkin](https://github.com/smoketurner/dropwizard-zipkin) [Java](dropwizard/src/main/java/brave/example/ExampleApplication.java) [Yaml](dropwizard/src/main/resources/server.yml)

* [jersey2-cassandra3](jersey2-cassandra3) `BRAVE_EXAMPLE=jersey2-cassandra3 docker-compose up`
  * Runtime: JaxRS 2, Jersey 2.32, DataStax Java Driver 3.0, Apache Cassandra 3.11, SLF4J 1.7, JRE 8
  * Trace Instrumentation: [Jersey Server](https://github.com/openzipkin/brave/tree/master/instrumentation/jersey-server), [DataStax Java Driver](https://github.com/openzipkin/brave-cassandra/tree/master/cassandra-driver), [Apache Cassandra](https://github.com/openzipkin/brave-cassandra/tree/master/cassandra), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Brave API](https://github.com/openzipkin/brave/tree/master/brave#setup) [XML](jersey2-cassandra3/src/main/webapp/WEB-INF/tracing.xml)

* [netty4-grpc](netty4-grpc) `BRAVE_EXAMPLE=netty4-grpc docker-compose up`
  * Runtime: Netty 4.1, Google gRPC 1.34  , SLF4J 1.7, JRE 15
  * Trace Instrumentation: [Netty Codec HTTP](https://github.com/openzipkin/brave/tree/master/instrumentation/netty-codec-http), [Google gRPC](https://github.com/openzipkin/brave/tree/master/instrumentation/grpc), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Brave API](https://github.com/openzipkin/brave/tree/master/brave#setup) [Java](netty4-grpc/src/main/java/brave/example/TracingConfiguration.java)

* [ratpack](ratpack) `BRAVE_EXAMPLE=ratpack docker-compose up`
  * Runtime: Ratpack 1.8, Guice 4, SLF4J 1.7, JRE 15
  * Trace Instrumentation: [Brave Ratpack](https://github.com/openzipkin-contrib/brave-ratpack)
  * Trace Configuration: [Brave Ratpack Guice](https://github.com/openzipkin-contrib/brave-ratpack) [Java](ratpack/src/main/java/brave/example/Backend.java)

* [webflux5-sleuth](webflux5-sleuth) `BRAVE_EXAMPLE=webflux5-sleuth docker-compose up`
  * Runtime: Spring 5.2, Reactor Netty 0.9, Spring Boot 2.3, Spring Cloud Sleuth 2.2, Log4J 2.13, JRE 15
  * Trace Instrumentation: [WebFlux Server](https://github.com/spring-cloud/spring-cloud-sleuth/blob/2.2.x/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/web/TraceWebFilter.java), [WebFlux Client](https://github.com/spring-cloud/spring-cloud-sleuth/blob/2.2.x/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/web/client/TraceWebClientBeanPostProcessor.java), [Reactor Context](https://github.com/spring-cloud/spring-cloud-sleuth/blob/2.2.x/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/reactor/ScopePassingSpanSubscriber.java), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Spring Cloud Sleuth](https://github.com/spring-cloud/spring-cloud-sleuth/tree/2.2.x/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/autoconfig) [Properties](webflux5-sleuth/src/main/resources/application.properties)

* [webmvc25-jetty](webmvc25-jetty) `BRAVE_EXAMPLE=webmvc25-jetty docker-compose up`
  * Runtime: Spring 2.5, Apache HttpClient 4.3, Servlet 2.5, Jetty 7.6, Log4J 1.2, JRE 6
  * Trace Instrumentation: [Servlet](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet), [Spring MVC](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc), [Apache HttpClient](https://github.com/openzipkin/brave/tree/master/instrumentation/httpclient), [Log4J 1.2](https://github.com/openzipkin/brave/tree/master/context/log4j12)
  * Trace Configuration: [Brave Spring Beans](https://github.com/openzipkin/brave/tree/master/spring-beans#configuration) [XML](webmvc25-jetty/src/main/webapp/WEB-INF/applicationContext.xml)

* [webmvc3-jetty](webmvc3-jetty) `BRAVE_EXAMPLE=webmvc3-jetty docker-compose up`
  * Runtime: Spring 3.2, Apache HttpClient 4.3, Servlet 3.0, Jetty 8.1, Log4J 1.2, JRE 7
  * Trace Instrumentation: [Servlet](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet), [Spring MVC](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc), [Spring Web](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-web), [Apache HttpClient](https://github.com/openzipkin/brave/tree/master/instrumentation/httpclient), [Log4J 1.2](https://github.com/openzipkin/brave/tree/master/context/log4j12)
  * Trace Configuration: [Brave Spring Beans](https://github.com/openzipkin/brave/tree/master/spring-beans#configuration) [XML](webmvc3-jetty/src/main/webapp/WEB-INF/applicationContext.xml)

* [webmvc4-jetty](webmvc4-jetty) `BRAVE_EXAMPLE=webmvc4-jetty docker-compose up`
  * Runtime: Spring 4.3, OkHttp 3.12, Jetty 9.2, Servlet 3.1, SLF4J 1.7, JRE 8
  * Trace Instrumentation: [Servlet](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet), [Spring MVC](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc), [Spring Web](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-web), [OkHttp](https://github.com/openzipkin/brave/tree/master/instrumentation/okhttp3), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Brave API](https://github.com/openzipkin/brave/tree/master/brave#setup) [Spring Java Config](webmvc4-jetty/src/main/java/brave/example/TracingConfiguration.java)

* [webmvc4-boot](webmvc4-boot) `BRAVE_EXAMPLE=webmvc4-boot docker-compose up`
  * Runtime: Spring 4.3, OkHttp 3.14, Spring Boot 1.5, Servlet 3.1, Jetty 9.4, SLF4J 1.7, JRE 8
  * Trace Instrumentation: [Servlet](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet), [Spring MVC](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc), [Spring Web](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-web), [OkHttp](https://github.com/openzipkin/brave/tree/master/instrumentation/okhttp3), [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * Trace Configuration: [Brave API](https://github.com/openzipkin/brave/tree/master/brave#setup) [Spring Boot AutoConfiguration](webmvc4-boot/src/main/java/brave/example/TracingAutoConfiguration.java)

## Starting the services with Docker

[Docker Compose](https://docs.docker.com/compose/) is the easiest way to start.

Just run `docker-compose up`.

Armeria starts by default. To use a different version of the project, set the `VERSION` variable.

Ex. `VERSION=webmvc25-jetty docker-compose up`

## Starting the services from source

When not using Docker, you'll need to start services according to the frameworks used.

First, start [Zipkin](https://zipkin.io/). This stores and queries traces
reported by the example services. 

Starting Zipkin with Java:
```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

### Java
In a separate tab or window, start each of `brave.example.Frontend` and `brave.example.Backend`.

Ex.
```bash
$ cd armeria
$ mvn compile exec:java -Dexec.mainClass=brave.example.Backend
$ mvn compile exec:java -Dexec.mainClass=brave.example.Frontend
```

### Servlet
In a separate tab or window, start a Jetty container for "backend" and "frontend".

Ex.
```bash
$ cd webmvc4-jetty
$ mvn jetty:run -Pfrontend
$ mvn jetty:run -Pbackend
```

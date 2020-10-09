# Basic example showing distributed tracing across HTTP applications
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
* This calls the backend (http://localhost:9000/api) and shows its result: a formatted date.

Afterwards, you can view traces that went through the backend via http://localhost:9411/zipkin?serviceName=backend
* This is a locally run zipkin service which keeps traces in memory

## Example projects

Here are the example projects you can try:

* [armeria](armeria)
  * [Armeria](https://armeria.dev/)
  * [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * [Java configuration](armeria/src/main/java/brave/example/HttpTracingFactory.java)
  * JRE 15 Runtime
* [webmvc25-jetty](webmvc25-jetty)
  * [Servlet 2.5](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet)
  * [Spring MVC 3](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc)
  * [Apache HttpClient](https://github.com/openzipkin/brave/tree/master/instrumentation/httpclient)
  * [Log4J 1.2](https://github.com/openzipkin/brave/tree/master/context/log4j12)
  * [Spring XML configuration](webmvc25-jetty/src/main/webapp/WEB-INF/applicationContext.xml)
  * Jetty 7/JRE 6 Runtime
* [webmvc3-jetty](webmvc3-jetty)
  * [Servlet 3.0](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet)
  * [Spring MVC 3](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc)
  * [Apache HttpClient](https://github.com/openzipkin/brave/tree/master/instrumentation/httpclient)
  * [Log4J 1.2](https://github.com/openzipkin/brave/tree/master/context/log4j12)
  * [Spring XML configuration](webmvc3-jetty/src/main/webapp/WEB-INF/applicationContext.xml)
  * Jetty 8/JRE 7 Runtime
* [webmvc4-jetty](webmvc4-jetty)
  * [Servlet 3.1](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet)
  * [Spring MVC 4](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc)
  * [OkHttp 3](https://github.com/openzipkin/brave/tree/master/instrumentation/okhttp3)
  * [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * [Spring Java Config](webmvc4-jetty/src/main/java/brave/example/TracingConfiguration.java)
  * Jetty 9/JRE 8 Runtime
* [webmvc4-boot](webmvc4-boot)
  * [Servlet 3.1](https://github.com/openzipkin/brave/tree/master/instrumentation/servlet)
  * [Spring MVC 4](https://github.com/openzipkin/brave/tree/master/instrumentation/spring-webmvc)
  * [OkHttp 3](https://github.com/openzipkin/brave/tree/master/instrumentation/okhttp3)
  * [SLF4J](https://github.com/openzipkin/brave/tree/master/context/slf4j)
  * [Spring Boot AutoConfiguration](webmvc4-boot/src/main/java/brave/example/TracingAutoConfiguration.java)
  * Spring Boot 1.5/Jetty 9/JRE 8 Runtime

## Starting the services with Docker

[Docker Compose](https://docs.docker.com/compose/) is the easiest way to start.

Just run `docker-compose up`.

Armeria starts by default. To use a different project, set the `PROJECT` variable first.

Ex. `PROJECT=webmvc25-jetty docker-compose up`

## Starting the services with Java

When not using Docker, you'll need to start services according to the frameworks used.

First, start [Zipkin](https://zipkin.io/). This stores and queries traces
reported by the example services. 

Starting Zipkin with Java:
```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

### Armeria
In a separate tab or window, start each of [brave.example.Frontend](/armeria/src/main/java/brave/example/Frontend.java)
and [brave.example.Backend](/armeria/src/main/java/brave/example/Backend.java):
```bash
$ cd armeria
$ mvn compile exec:java -Dexec.mainClass=brave.example.Backend
$ mvn compile exec:java -Dexec.mainClass=brave.example.Frontend
```

### Servlet
In a separate tab or window, start each of [brave.example.Frontend](/webmvc4-jetty/src/main/java/brave/example/Frontend.java)
and [brave.example.Backend](/webmvc4-jetty/src/main/java/brave/example/Backend.java):
```bash
$ cd webmvc4-jetty
$ mvn jetty:run -Pfrontend
$ mvn jetty:run -Pbackend
```

### Spring Boot
In a separate tab or window, start each of [brave.example.Frontend](/webmvc4-boot/src/main/java/brave/example/Frontend.java)
and [brave.example.Backend](/webmvc4-boot/src/main/java/brave/example/Backend.java):
```bash
$ cd webmvc4-boot
$ mvn compile exec:java -Dexec.mainClass=brave.example.Backend
$ mvn compile exec:java -Dexec.mainClass=brave.example.Frontend
```

## Tips

There are some interesting details that apply to all examples:
* If you pass the header `user_name` Brave will automatically propagate it to the backend!
  * `curl -s localhost:8081 -H'user_name: JC'`
* The below Logback pattern adds trace and span identifiers into log output
  * `%d{HH:mm:ss.SSS} [%thread] [%X{userName}] [%X{traceId}/%X{spanId}] %-5level %logger{36} - %msg%n`

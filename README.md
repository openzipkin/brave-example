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

Once the services are started, open http://localhost:8081/
* This calls the backend (http://localhost:9000/api) and shows its result: a formatted date.

Afterwards, you can view traces that went through the backend via http://localhost:9411/?serviceName=backend
* This is a locally run zipkin service which keeps traces in memory

## Starting the services

First, start [Zipkin](https://zipkin.io/). This stores and queries traces
reported by the example services. 

Starting Zipkin with Java:
```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

### Armeria

Here are our [Armeria](https://armeria.dev/) examples:
* [HTTP](armeria) - uses Armeria APIs to serve and invoke HTTP requests

#### Starting Armeria with Java
In a separate tab or window, start each of [brave.example.Frontend](/armeria/src/main/java/brave/example/Frontend.java)
and [brave.example.Backend](/armeria/src/main/java/brave/example/Backend.java):
```bash
$ cd armeria
$ mvn compile exec:java -Dexec.mainClass=brave.example.Backend
$ mvn compile exec:java -Dexec.mainClass=brave.example.Frontend
```

### Servlet
Our Servlet examples use [Jetty](https://www.eclipse.org/jetty/) and
[Spring MVC](https://spring.io/guides/gs/rest-service/) controllers. These
examples primarily show how configuration works in XML or Java configuration:
* [WebMVC 2.5](webmvc25-jetty) - Spring XML on Servlet 2.5 container
* [WebMVC 3](webmvc3-jetty) - Spring XML on Servlet 3 container
* [WebMVC 4](webmvc4-jetty) - Spring Java Config on Servlet 3 container

#### Starting Jetty with Java
In a separate tab or window, start each of [brave.example.Frontend](/webmvc4-jetty/src/main/java/brave/example/Frontend.java)
and [brave.example.Backend](/webmvc4-jetty/src/main/java/brave/example/Backend.java):
```bash
$ cd webmvc4-jetty
$ mvn jetty:run -Pfrontend
$ mvn jetty:run -Pbackend
```

### Spring Boot
Here are our Spring Boot examples:
* [Spring Boot 1.5](webmvc4-boot) - [Spring MVC 4](https://spring.io/guides/gs/rest-service/)

#### Starting Spring Boot with Java
In a separate tab or window, start each of [brave.example.Frontend](/webmvc4-boot/src/main/java/brave/example/Frontend.java)
and [brave.example.Backend](/webmvc4-boot/src/main/java/brave/example/Backend.java):
```bash
$ cd webmvc4-boot
$ mvn compile exec:java -Dexec.mainClass=brave.example.Backend
$ mvn compile exec:java -Dexec.mainClass=brave.example.Frontend
```

## Configuration tips

There are some interesting details that apply to both
* If you pass the header `user_name` Brave will automatically propagate it to the backend!
  * `curl -s localhost:8081 -H'user_name: JC'`
* The below pattern adds trace and span identifiers into log output
  * `%d{ABSOLUTE} [%X{traceId}/%X{spanId}] %-5p [%t] %C{2} (%F:%L) - %m%n`

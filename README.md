# Basic example showing distributed tracing across servlet applications
This is an example app where two Servlet (Java) services collaborate on
an http request. Notably, timing of these requests are recorded into
[Zipkin](http://zipkin.io/), a distributed tracing system. This allows
you to see the how long the whole operation took, as well how much time
was spent in each service.

Here's an example of what it looks like:

<img width="979" alt="zipkin screen shot" src="https://user-images.githubusercontent.com/64215/31760971-18a9a4dc-b4bf-11e7-9ee2-ab7418f80dc0.png">

This example was ported from https://github.com/openzipkin/sleuth-webmvc-example

# Implementation Overview

Web requests are served by [Spring MVC](https://spring.io/guides/gs/rest-service/) controllers,
and tracing is automatically performed for you by [Brave](https://github.com/openzipkin/brave).

This example intentionally avoids advanced topics like async and messaging,
eventhough Brave supports that, too. Once you get familiar with things,
you can play with more interesting [Brave instrumentation](https://github.com/openzipkin/brave/tree/master/instrumentation).

# Running the example
This example has two services: frontend and backend. They both report trace data to zipkin. To setup the demo, you need to start Frontend, Backend and Zipkin.

Once the services are started, open http://localhost:8081/
* This will call the backend (http://localhost:9000/api) and show the result, which defaults to a formatted date.

Next, you can view traces that went through the backend via http://localhost:9411/?serviceName=backend
* This is a locally run zipkin service which keeps traces in memory

## Starting the Services
In a separate tab or window, start each of [brave.webmvc.Frontend](/servlet3/src/main/java/brave/webmvc/Frontend.java)
and [brave.webmvc.Backend](/servlet3/src/main/java/brave/webmvc/Backend.java):
```bash
# choose servlet25 or servlet3
$ cd servlet3
$ mvn jetty:run -Pfrontend
$ mvn jetty:run -Pbackend
```

Next, run [Zipkin](http://zipkin.io/), which stores and queries traces
reported by the above services.

```bash
wget -O zipkin.jar 'https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec'
java -jar zipkin.jar
```

## Configuration tips
To show how wiring works, we have two copies of the same project
* [Servlet 2.5](./servlet25)
* [Servlet 3](./servlet3)

There are some interesting details that apply to both
* If you pass the header `user-name` Brave will automatically propagate it to the backend!
  * `curl -s localhost:8081 -H'user-name: JC'`
* The below pattern adds trace and span identifiers into log output
  * `%d{ABSOLUTE} [%X{traceId}/%X{spanId}] %-5p [%t] %C{2} (%F:%L) - %m%n`

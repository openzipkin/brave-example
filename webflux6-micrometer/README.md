## Tracing Example: Spring 6/Reactor Netty/Spring Boot 3/Micrometer/Log4J 2

Instead of servlet, this uses Spring Boot 3 to create a self-contained
application that runs Spring WebFlux 6 controllers.

* brave.example.Frontend and Backend: Rest controllers
* brave.example.AppAutoConfiguration: Sets up the WebClient used in the Frontend.

Application code doesn't show any tracing configuration because that's handled
by [Micrometer Tracing](https://docs.micrometer.io/tracing/reference/index.html),
configured by [Spring Boot](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/#actuator.micrometer-tracing.tracer-implementations.brave-zipkin).

Micrometer Tracing has its own Tracer API that can bridge to Brave's with a
plugin. This allows users to mix Micrometer with native Brave instrumentation.
However, this example does not use any native Brave instrumentation.

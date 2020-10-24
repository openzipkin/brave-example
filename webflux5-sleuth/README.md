## Tracing Example: Spring WebFlux 5/Spring Boot 2.3/Spring Cloud Sleuth 2.2

Instead of servlet, this uses Spring Boot 2.3 to create a self-contained
application that runs Spring WebFlux 5 controllers.

* brave.example.Frontend and Backend: Rest controllers
* brave.example.AppAutoConfiguration: Sets up the WebClient used in the Frontend.

Application code doesn't show any tracing configuration because that's handled
by [Spring Cloud Sleuth v2](https://github.com/spring-cloud/spring-cloud-sleuth/tree/2.2.x), configured by properties.

Version 2 of Sleuth was based on Brave. It provided auto-configuration of
Brave libraries used in other projects, such as span reporting, logging context,
sampling and instrumentation. This allowed end users to use properties for
configuration instead of Java Config.

In some cases, Sleuth used code that used Brave APIs, but not maintained by
OpenZipkin. This example uses Sleuth's code for reporting spans and WebFlux. 

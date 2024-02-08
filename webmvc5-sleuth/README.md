## Tracing Example: Spring 5/Spring Boot 2/Spring Cloud Sleuth 3/SLF4J 1.7/JDK 8

Instead of servlet, this uses Spring Boot 2 to create a self-contained
application that runs Spring WebMvc 5 controllers.

* brave.example.Frontend and Backend: Rest controllers
* brave.example.AppAutoConfiguration: Sets up the RestTemplate used in the Frontend.

Application code doesn't show any tracing configuration because that's handled
by [Spring Cloud Sleuth v3](https://github.com/spring-cloud/spring-cloud-sleuth/tree/3.1.x), configured by properties.

Version 2 of Sleuth was based on Brave. It provided auto-configuration of
Brave libraries used in other projects, such as span reporting, logging context,
sampling and instrumentation. This allowed end users to use properties for
configuration instead of Java Config.

In some cases, Sleuth used code that used Brave APIs, but not maintained by
OpenZipkin. This example uses Sleuth's code for reporting spans and WebFlux. 

### Service Discovery with Eureka

This supports discovery of zipkin via [Eureka](https://github.com/Netflix/eureka).
To do so, set `EUREKA_SERVICE_URL` to a possibly authenticated v2 endpoint.

Here are some examples:
* Eureka is on localhost: `EUREKA_SERVICE_URL=http://localhost:8761/eureka/v2`
* Eureka is authenticated: `EUREKA_SERVICE_URL=http://username:password@localhost:8761/eureka/v2`

Note: `EUREKA_SERVICE_URL` includes the '/v2' suffix, even though [eureka.client.serviceUrl.defaultZone](https://cloud.spring.io/spring-cloud-static/Edgware.SR6/multi/multi__service_discovery_eureka_clients.html#_registering_with_eureka)
does not. This is to be consistent with other examples that don't use Spring.

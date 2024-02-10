## Tracing Example: Spring 5/Spring Boot 2/Spring Cloud Sleuth 3/SLF4J 1.7/JDK 8

Instead of servlet, this uses Spring Boot 2 to create a self-contained
application that runs Spring WebFlux 5 controllers.

* brave.example.Frontend and Backend: Rest controllers
* brave.example.AppAutoConfiguration: Sets up the RestTemplate used in the Frontend.

Application code doesn't show any tracing configuration because that's handled
by [Spring Cloud Sleuth v3](https://github.com/spring-cloud/spring-cloud-sleuth/tree/3.1.x), configured by properties.

Sleuth 3.x is based on Brave, providing autoconfiguration of Brave libraries
used in other projects, such as span reporting, logging context, sampling and
instrumentation. This allowed end users to use properties for configuration
instead of Java Config.

In some cases, Sleuth has custom code that used Brave APIs. This example uses
Sleuth-specific logic for instrumenting WebFlux and reporting spans to Zipkin.

### Zipkin Reporter 2.x

Starting with version 6, Brave no longer has a dependency on zipkin-reporter,
so it can work with 2.x or 3.x. To maintain compatability with existing
autoconfiguration, Sleuth requires io.zipkin.reporter2:zipkin-reporter 2.x.
Hence, this project overrides the Brave version to 6.x, but leaves the reporter
version managed by Sleuth.

### Service Discovery with Eureka

This supports discovery of zipkin via [Eureka](https://github.com/Netflix/eureka).
To do so, set `EUREKA_SERVICE_URL` to a possibly authenticated v2 endpoint.

Here are some examples:
* Eureka is on localhost: `EUREKA_SERVICE_URL=http://localhost:8761/eureka/v2`
* Eureka is authenticated: `EUREKA_SERVICE_URL=http://username:password@localhost:8761/eureka/v2`

Note: `EUREKA_SERVICE_URL` includes the '/v2' suffix, even though [eureka.client.serviceUrl.defaultZone](https://cloud.spring.io/spring-cloud-static/Edgware.SR6/multi/multi__service_discovery_eureka_clients.html#_registering_with_eureka)
does not. This is to be consistent with other examples that don't use Spring.

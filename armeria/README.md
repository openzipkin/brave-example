## Tracing Example: Armeria

Instead of servlet, this uses [Armeria](https://armeria.dev/) to serve HTTP
requests. Both services run as a normal Java application.

*   brave.example.Frontend and Backend : HTTP controllers with a trace configuration hook
*   brave.example.HttpTracingFactory : Configures the tracing subsystem

### Service Discovery with Eureka

This supports discovery of zipkin via [Eureka](https://github.com/Netflix/eureka).
To do so, set `EUREKA_SERVICE_URL` to a possibly authenticated v2 endpoint.

Here are some examples:
* Eureka is on localhost: `EUREKA_SERVICE_URL=http://localhost:8761/eureka/v2`
* Eureka is authenticated: `EUREKA_SERVICE_URL=http://username:password@localhost:8761/eureka/v2`

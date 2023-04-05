## Tracing Example: Armeria and HTTP Client

Instead of servlet, this uses [Armeria](https://armeria.dev/) to serve HTTP
requests, and [Apache HTTP Client](https://hc.apache.org/httpcomponents-client-4.5.x/) to call backend endpoint.
Both services run as a normal Java application.

*   brave.example.Frontend and Backend : HTTP controllers with a trace configuration hook
*   brave.example.HttpTracingFactory : Configures the tracing subsystem

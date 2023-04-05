## Tracing Example: Armeria

Instead of servlet, this uses [Armeria](https://armeria.dev/) to serve HTTP
requests. Both services run as a normal Java application.

*   brave.example.Frontend and Backend : HTTP controllers with a trace configuration hook
*   brave.example.HttpTracingFactory : Configures the tracing subsystem

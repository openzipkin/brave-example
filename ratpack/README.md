## Tracing Example: Ratpack

Instead of servlet, this uses [Ratpack](https://ratpack.io/) to serve HTTP
requests. Both services run as a normal Java application.

*   brave.example.Frontend and Backend : Main classes that define Ratpack HTTP handlers
*   brave.example.BraveModule : config properties that can be overridden via ratpack.brave.X
*   brave.example.ZipkinModule : config properties that can be overridden via ratpack.zipkin.X

## WebMVC 4 Boot Example

Instead of servlet, this uses Spring Boot to create a self-contained
application.

*   brave.webmvc.Frontend and Backend : Rest controllers with no tracing configuration
*   brave.webmvc.TracingConfiguration : This adds tracing by configuring the tracer, server and client tracing interceptors.

`TracingConfiguration` is automatically loaded due to `META-INF/spring.factories`
This allows the `Frontend` and `Backend` controllers to have no tracing
code. Inside the tracing configuration, you'll notice the rest template
is setup via a `RestTemplateCustomizer`, which ensures application-level
interceptors are not affected. Also, you'll notice layered tracing for
server requests. First, `TracingFilter` creates a span, then later,
`SpanCustomizingAsyncHandlerInterceptor` adds MVC tags to it.


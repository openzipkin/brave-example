## Tracing Example: Spring WebMVC 4/Spring Boot 1.5

Instead of servlet, this uses Spring Boot 1.5 to create a self-contained
application that runs Spring WebMVC 4 controllers.

*   brave.example.Frontend and Backend : Rest controllers with no tracing configuration
*   brave.example.TracingConfiguration : This adds tracing by configuring the tracer, server and client tracing interceptors.

`TracingConfiguration` is automatically loaded due to `META-INF/spring.factories`
This allows the `Frontend` and `Backend` controllers to have no tracing
code. Inside the tracing configuration, you'll notice the rest template
is setup via a `RestTemplateCustomizer`, which ensures application-level
interceptors are not affected. Also, you'll notice layered tracing for
server requests. First, `TracingFilter` creates a span, then later,
`SpanCustomizingAsyncHandlerInterceptor` adds MVC tags to it.

*Note* This only lightly configures tracing. When doing anything serious,
consider [Spring Cloud Sleuth](https://github.com/spring-cloud/spring-cloud-sleuth) instead.

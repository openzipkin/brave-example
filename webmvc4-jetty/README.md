## Tracing Example: Spring WebMVC 4/Servlet 3.1/Jetty 9

### ExampleInitializer

Instead of a web.xml file, this uses a Servlet 3.1
`ServletContainerInitializer` to setup the app and tracing of it.

`Initializer` is indirectly invoked by `SpringServletContainerInitializer`,
which is in the classpath. This sets up the following:

*   brave.example.Frontend and Backend : Rest controllers with no tracing configuration
*   brave.example.TracingConfiguration : This adds tracing by configuring the tracer, server and client tracing interceptors.



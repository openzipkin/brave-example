## WebMVC 4 Example

### ExampleInitializer

Instead of a web.xml file, this uses a Servlet 3.0
`ServletContainerInitializer` to setup the app and tracing of it.

`Initializer` is indirectly invoked by `SpringServletContainerInitializer`,
which is in the classpath. This sets up the following:

*   brave.webmvc.Frontend and Backend : Rest controllers with no tracing configuration
*   brave.webmvc.TracingConfiguration : This adds tracing by configuring the tracer, server and client tracing interceptors.



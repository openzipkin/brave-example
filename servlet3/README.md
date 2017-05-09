## Servlet 3.0 Example

### ExampleInitializer

Instead of a web.xml file, this uses a Servlet 3.0
`ServletContainerInitializer` to setup the app and tracing of it.

`ExampleInitializer` is indirectly invoked by `SpringServletContainerInitializer`,
which is in the classpath. This sets up the following:

*   brave.webmvc.ExampleController : This sets up the web controller and rest template and has no tracing configuration
*   brave.webmvc.WebTracingConfiguration : This adds tracing by configuring the tracer, server and client tracing interceptors.

### Undertow

Undertow is embedded and started in the setup method of our test (ITWebMvcExample) and stopped in the tearDown method.

Undertow is configured to look for `ExampleInitializer` specifically eventhough Servlet 3+ containers find it automatically.



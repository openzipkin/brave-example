## Servlet 2.5 Example

### spring-webmvc-servlet.xml

`spring-webmvc-servlet.xml` is indirectly read due to spring references
in web.xml. This setups the app and tracing of it.

*   brave.webmvc.ExampleController : This sets up the web controller and rest template and has no tracing configuration
*   brave.webmvc.TracingFactoryBean : This helps configure tracing, notably Log4J 1.2 integration

### Jetty

Jetty is embedded and started in the setup method of our test (ITWebMvcExample) and stopped in the tearDown method.

Jetty is configured to look for the xml files under [src/main/webapp/WEB-INF].



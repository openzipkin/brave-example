# brave-webmvc-example

Example Spring Web MVC service implementation that shows the use of the client and server side interceptors from the
`brave-spring-web-servlet-interceptor`  and `brave-spring-resttemplate-interceptors` modules and the usage of [brave](https://github.com/kristofa/brave) in general.

## What is it about?

On one hand there is the service resource which can be found in following class: 
`brave.webmvc.ExampleController`

Next to that there is an integration test: `brave.webmvc.ITWebMvcExample` that sets up
an embedded Jetty server at port `8081` which deploys our service resource at context path http://localhost:8081.

The resource (WebExampleController) makes available 2 URI's

*   GET http://localhost:8081/a
*   GET http://localhost:8081/b

The test code (ITWebMvcExample) sets up our endpoint, does a http GET request to http://localhost:8081/a
The code that is triggered through this URI will make a new call to the other URI: http://localhost:8081/b

For both requests our client and server side interceptors that use the Brave api are executed.  This results in 2 spans being reported.
The test uses log4j2 integration which associates span IDs in log output:

```
16:34:39,858 [79270535a7e54a1f/79270535a7e54a1f] INFO  [qtp112302969-17] webmvc.ExampleController - in /a
16:34:40,347 [79270535a7e54a1f/a710aa8929a7f038] INFO  [qtp112302969-18] webmvc.ExampleController - in /b
```

Here's what the log says:

1.  The first span is the root of the trace (notice the trace ID and span ID are the same).
2.  The second span is a child of that (notice the same trace ID, but a different span ID).

## Running the example

Run [Zipkin](http://zipkin.io/), which stores and queries traces reported by the above services.

```bash
wget -O zipkin.jar 'https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec'
java -jar zipkin.jar
```

Next, from this directory, run `mvn verify`, which will run the test scenario (ITWebMvcExample)

Finally, browse zipkin for the traces the test created: http://localhost:9411/

## How is it all hooked together?

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

## Deploying to Tomcat

From this directory, run `mvn package`, which creates a war file.

Next, make it the root war in tomcat. Ex. `cp target/*war $CATALINA_HOME/webapps/ROOT.war`

Ensure zipkin is running and hit the tomcat URL to the path /a. Ex. http://localhost:8080/a

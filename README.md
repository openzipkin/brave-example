# Basic example showing distributed tracing across Armeria apps
This is an example app where two Armeria (Java) services collaborate on an http request. Notably, timing of these requests are recorded into [Zipkin](http://zipkin.io/), a distributed tracing system. This allows you to see the how long the whole operation took, as well how much time was spent in each service.

Here's an example of what it looks like
<img width="960" alt="zipkin screen shot" src="https://user-images.githubusercontent.com/64215/40405563-1885eb04-5e98-11e8-9bd6-53bcd09711d2.png">

# Implementation Overview

Web requests are served by [Armeria](https://line.github.io/armeria/server-basics.html) services, which trace requests by using the [Zipkin plugin](https://line.github.io/armeria/advanced-zipkin.html).

These traces are sent out of process over http to Zipkin.

This example intentionally avoids advanced topics like async and load balancing, eventhough Armeria supports them.

# Running the example
This example has two services: frontend and backend. They both report trace data to zipkin. To setup the demo, you need to start Frontend, Backend and Zipkin.

Once the services are started, open http://localhost:8081/
* This will call the backend (http://localhost:9000/api) and show the result, which defaults to a formatted date.

Next, you can view traces that went through the backend via http://localhost:9411/?serviceName=backend
* This is a locally run zipkin service which keeps traces in memory

## Starting the Services
In a separate tab or window, start each of [armeria.Frontend](/src/main/java/armeria/Frontend.java) and [armeria.Backend](/src/main/java/armeria/Backend.java):
```bash
$ mvn compile exec:java -Dexec.mainClass=armeria.Frontend
$ mvn compile exec:java -Dexec.mainClass=armeria.Backend
```

Next, run [Zipkin](http://zipkin.io/), which stores and queries traces reported by the above services.

```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

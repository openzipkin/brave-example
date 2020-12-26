## Tracing Example: Netty 4.1/Google gRPC 1.34/JRE 15

Instead of servlet, the frontend is a Netty HTTP handler. The backend is Google gRPC 1.31.
Both services run as a normal Java application.

* [brave.example.Frontend](src/main/java/brave/example/Frontend.java) - HTTP controller and Google gRPC client
* [brave.example.Backend](src/main/java/brave/example/Backend.java) - Google gRPC server
* [brave.example.TracingConfiguration](src/main/java/brave/example/TracingConfiguration.java) - Configures trace instrumentation

Here's an example screen shot:
![screen shot](https://user-images.githubusercontent.com/64215/102683005-a2986500-4208-11eb-8258-92cc02f9310b.png)

This example also shows Brave's `BaggagePropagation`. If you make a request to the frontend with
the HTTP header `user_name`, it will transparently propagate and be readable even in gRPC.

Ex. The backend adds the user to the response, only if it can see a header that was sent to the
frontend. This shows transparent propagation across different frameworks.

```bash
$ curl -s localhost:8081 -H"user_name: joey"
Sat Dec 19 16:11:01 MYT 2020 joey
```

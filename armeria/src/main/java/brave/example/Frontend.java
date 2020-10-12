package brave.example;

import brave.http.HttpTracing;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.client.brave.BraveClient;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.LoggingService;

public final class Frontend {

  public static void main(String[] args) {
    final HttpTracing httpTracing = HttpTracingFactory.create("frontend");

    final WebClient backendClient =
        WebClient.builder(System.getProperty("backend.endpoint", "http://127.0.0.1:9000/api"))
            .decorator(BraveClient.newDecorator(httpTracing.clientOf("backend")))
            .build();

    final Server server =
        Server.builder()
            .http(8081)
            .service("/health", HealthCheckService.builder().build())
            .service("/", (ctx, req) -> backendClient.get(""))
            .decorator(BraveService.newDecorator(httpTracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

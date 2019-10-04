package armeria;

import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.HttpClientBuilder;
import com.linecorp.armeria.client.brave.BraveClient;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.logging.LoggingService;

import brave.Tracing;

public final class Frontend {

  public static void main(String[] args) {
    final Tracing tracing = TracingFactory.create("frontend");

    final HttpClient backendClient =
        new HttpClientBuilder("http://localhost:9000/")
            .decorator(BraveClient.newDecorator(tracing, "backend"))
            .build();

    final Server server =
        Server.builder()
              .http(8081)
              .service("/", (ctx, req) -> backendClient.get("/api"))
              .decorator(BraveService.newDecorator(tracing))
              .decorator(LoggingService.newDecorator())
              .build();

    server.start().join();
  }
}

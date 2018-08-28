package armeria;

import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.HttpClientBuilder;
import com.linecorp.armeria.client.tracing.HttpTracingClient;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.tracing.HttpTracingService;

import brave.Tracing;

public final class Frontend {

  public static void main(String[] args) {
    final Tracing tracing = TracingFactory.create("frontend");

    final HttpClient backendClient =
        new HttpClientBuilder("http://localhost:9000/")
            .decorator(HttpTracingClient.newDecorator(tracing, "backend"))
            .build();

    final Server server =
        new ServerBuilder()
            .http(8081)
            .service("/", (ctx, res) -> backendClient.get("/api"))
            .decorator(HttpTracingService.newDecorator(tracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

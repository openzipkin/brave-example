package armeria;

import brave.Tracing;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.HttpClientBuilder;
import com.linecorp.armeria.client.tracing.HttpTracingClient;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.tracing.HttpTracingService;

public class Frontend {

  public static void main(String[] args) {
    Tracing tracing = TracingFactory.create("frontend");

    HttpClient backendClient =
        new HttpClientBuilder("http://localhost:9000/")
            .decorator(HttpTracingClient.newDecorator(tracing, "backend"))
            .build();

    Server server =
        new ServerBuilder()
            .http(8081)
            .service("/", (ctx, res) -> backendClient.get("/api"))
            .decorator(HttpTracingService.newDecorator(tracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

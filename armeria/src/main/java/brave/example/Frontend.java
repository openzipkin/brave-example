package brave.example;

import brave.http.HttpTracing;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.client.brave.BraveClient;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Frontend {
  static final Logger LOGGER = LoggerFactory.getLogger(HttpTracingFactory.class);

  public static void main(String[] args) {
    final HttpTracing httpTracing = HttpTracingFactory.create("frontend");

    final String backendEndpoint =
        System.getProperty("backend.endpoint", "http://127.0.0.1:9000/api");
    LOGGER.info("Using backend endpoint: {}", backendEndpoint);

    final WebClient backendClient =
        WebClient.builder(backendEndpoint)
            .decorator(BraveClient.newDecorator(httpTracing.clientOf("backend")))
            .build();

    final AccessLogWriter accessLogWriter =
        HttpTracingFactory.accessLogWriter(httpTracing, AccessLogWriter.common());

    final Server server =
        Server.builder()
            .http(8081)
            .accessLogWriter(accessLogWriter, true)
            .service("/health", HealthCheckService.builder().build())
            .service("/", (ctx, req) -> backendClient.get(""))
            .decorator(BraveService.newDecorator(httpTracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

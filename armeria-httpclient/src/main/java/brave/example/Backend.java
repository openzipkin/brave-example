package brave.example;

import brave.http.HttpTracing;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.LoggingService;
import java.util.Date;

public final class Backend {

  public static void main(String[] args) {
    final HttpTracing httpTracing = HttpTracingFactory.create("backend");

    final Server server = Server.builder()
        .http(9000)
        .service("/health", HealthCheckService.builder().build())
        .service("/api", (ctx, req) -> {
          String response = new Date().toString();
          String username = req.headers().get("user_name");
          if (username != null) response += " " + username;
          return HttpResponse.of(response);
        })
        .decorator(BraveService.newDecorator(httpTracing))
        .decorator(LoggingService.newDecorator())
        .build();

    server.start().join();
  }
}

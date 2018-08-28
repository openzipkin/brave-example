package armeria;

import java.util.Date;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.tracing.HttpTracingService;

import brave.Tracing;

public final class Backend {

  public static void main(String[] args) {
    final Tracing tracing = TracingFactory.create("backend");

    final Server server =
        new ServerBuilder()
            .http(9000)
            .service("/api", (ctx, res) -> HttpResponse.of(new Date().toString()))
            .decorator(HttpTracingService.newDecorator(tracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

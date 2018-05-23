package armeria;

import brave.Tracing;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.tracing.HttpTracingService;
import java.util.Date;

public class Backend {

  public static void main(String[] args) {
    Tracing tracing = TracingFactory.create("backend");

    Server server =
        new ServerBuilder()
            .http(9000)
            .service("/api", (ctx, res) -> HttpResponse.of(new Date().toString()))
            .decorator(HttpTracingService.newDecorator(tracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

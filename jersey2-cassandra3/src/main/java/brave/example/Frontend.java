package brave.example;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.common.net.HostAndPort;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.jersey.server.ResourceConfig;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Frontend {
  @Path("")
  public static class Resource {
    final Session session;
    final PreparedStatement preparedStatement;

    Resource(Session session) {
      this.session = session;
      // Normal backend example returns the date, possibly with "userName" baggage field.
      // There's no current way to select baggage inside CQL, so we skip it and just print the date.
      this.preparedStatement = session.prepare("SELECT toTimestamp(now()) from system.local");
    }

    @GET public String callBackend() {
      Date date = session.execute(preparedStatement.bind()).one().getTimestamp(0);
      return date + "\n";
    }
  }

  /** Fake /health endpoint that allows us to ensure our HEALTHCHECK doesn't start traces. */
  public static class HealthCheck implements HttpHandler {
    static final byte[] body = "ok\n".getBytes(UTF_8);

    @Override public void handle(HttpExchange exchange) throws IOException {
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    String contactPointString = System.getProperty("backend.contactPoint", "127.0.0.1:9042");
    HostAndPort parsed = HostAndPort.fromString(contactPointString).withDefaultPort(9042);
    Cluster cluster = Cluster.builder()
        .addContactPointsWithPorts(new InetSocketAddress(parsed.getHostText(), parsed.getPort()))
        .build();
    Runtime.getRuntime().addShutdownHook(new Thread(cluster::close));

    TracingConfiguration tracing = TracingConfiguration.create("frontend");
    ResourceConfig config = new ResourceConfig();
    config.register(new Resource(tracing.tracingSession(cluster.connect(), "backend")));
    config.register(tracing.tracingListener());
    HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(config, HttpHandler.class);

    HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
    server.createContext("/", handler);
    server.createContext("/health", new HealthCheck());
    server.start();
    Thread.currentThread().join();
  }
}

package brave.example;

import brave.cassandra.Tracing;
import java.io.File;
import org.apache.cassandra.service.CassandraDaemon;

/**
 * We construct arguments to {@link CassandraDaemon} in the typically named entrypoint class for a
 * couple reasons. One is to keep examples decoupled from implementation details. Another is that
 * this allows someone to run this in their IDE easily, without needing to know special knowledge
 * about Cassandra. For example set a debugger break point in {@link Tracing}, if they want to see
 * what's going on.
 */
public final class Backend {
  public static void main(String[] args) {
    // Setup basic Cassandra properties that could otherwise be set at runtime.
    String port = System.getenv("PORT");
    if (port != null) System.setProperty("cassandra.native_transport_port", port);
    File scratchDir = new File(System.getProperty("user.dir") + "/target");
    if (!scratchDir.exists()) scratchDir.mkdir();
    System.setProperty("cassandra-foreground", "yes");
    System.setProperty("cassandra.storagedir", scratchDir.getAbsolutePath());
    System.setProperty("cassandra.triggers_dir", scratchDir.getAbsolutePath());
    System.setProperty("cassandra.config", "cassandra.yaml");

    // Tracing integration is via "cassandra.custom_tracing_class", which defaults to initialize
    // statically. We are using this approach solely to show out-of-box experience.
    // The implementation can be extended to initialize from Spring or another config provider.
    System.setProperty("zipkin.http_endpoint",
        System.getProperty("zipkin.baseUrl", "http://127.0.0.1:9411") + "/api/v2/spans");
    System.setProperty("zipkin.service_name",
        System.getProperty("brave.localServiceName", "backend"));
    System.setProperty("cassandra.custom_tracing_class", Tracing.class.getName());

    // Dispatch to the normal Cassandra entrypoint class
    CassandraDaemon.main(args);
  }
}

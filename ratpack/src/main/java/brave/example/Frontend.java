package brave.example;

import com.google.common.collect.ImmutableMap;
import com.google.inject.name.Names;
import java.net.URI;
import javax.inject.Inject;
import javax.inject.Named;
import ratpack.exec.Promise;
import ratpack.guice.Guice;
import ratpack.http.client.HttpClient;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.zipkin.ServerTracingModule;
import ratpack.zipkin.Zipkin;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

public final class Frontend {
  final HttpClient client;
  final URI backendEndpoint;

  @Inject Frontend(@Zipkin HttpClient client, @Named("backendEndpoint") String backendEndpoint) {
    this.client = client;
    this.backendEndpoint = URI.create(backendEndpoint);
  }

  Promise<String> callBackend() {
    return client.get(backendEndpoint).map(response -> response.getBody().getText());
  }

  public static void main(String[] args) throws Exception {
    ServerConfig serverConfig = ServerConfig.embedded()
        .props(ImmutableMap.of(
            "brave.localServiceName", "frontend",
            "backend.endpoint", "http://127.0.0.1:9000/api"))
        .sysProps() // allows overrides like ratpack.zipkin.baseUrl=...
        .port(8081)
        .build();

    Reporter<Span> spanReporter = serverConfig.get("/zipkin", ZipkinConfig.class).toSpanReporter();
    RatpackServer.start(server -> server.serverConfig(serverConfig)
        .registry(Guice.registry(bindings -> bindings
            .moduleConfig(ServerTracingModule.class, serverConfig.get("/brave", BraveConfig.class)
                .setSpanReporter(spanReporter).toModuleConfig())
            .binder(b -> b.bind(String.class).annotatedWith(Names.named("backendEndpoint"))
                .toInstance(serverConfig.get("/backend/endpoint", String.class)))
            .bind(Frontend.class)
        ))
        .handlers(chain -> chain
            .get("health", ctx -> ctx.render("ok"))
            .get("", ctx -> ctx.render(ctx.get(Frontend.class).callBackend()))
        )
    );
  }
}

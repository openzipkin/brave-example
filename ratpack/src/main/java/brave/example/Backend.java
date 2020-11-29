package brave.example;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import ratpack.guice.Guice;
import ratpack.handling.Context;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.zipkin.ServerTracingModule;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

public final class Backend {

  String printDate(Context ctx) {
    String response = new Date().toString();
    Optional<String> username = ctx.header("user_name");
    if (username.isPresent()) response += " " + username.get();
    return response;
  }

  public static void main(String[] args) throws Exception {
    ServerConfig serverConfig = ServerConfig.embedded()
        .props(Collections.singletonMap("brave.localServiceName", "backend"))
        .sysProps() // allows overrides like ratpack.zipkin.baseUrl=...
        .port(9000)
        .build();

    Reporter<Span> spanReporter = serverConfig.get("/zipkin", ZipkinConfig.class).toSpanReporter();
    RatpackServer.start(server -> server.serverConfig(serverConfig)
        .registry(Guice.registry(bindings -> bindings
            .moduleConfig(ServerTracingModule.class, serverConfig.get("/brave", BraveConfig.class)
                .setSpanReporter(spanReporter).toModuleConfig())
            .bind(Backend.class)
        ))
        .handlers(chain -> chain
            .get("health", ctx -> ctx.render("ok"))
            .get("api", ctx -> ctx.render(ctx.get(Backend.class).printDate(ctx)))
        )
    );
  }
}

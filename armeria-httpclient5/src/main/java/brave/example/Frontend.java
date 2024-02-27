package brave.example;

import brave.http.HttpTracing;
import brave.httpclient5.HttpClient5Tracing;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.LoggingService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class Frontend {

    public static void main(String[] args) {
        final HttpTracing httpTracing = HttpTracingFactory.create("frontend");

        CloseableHttpClient backendClient = HttpClient5Tracing.newBuilder(httpTracing)
                .build(HttpClients.custom());

        final Server server =
                Server.builder()
                        .http(8081)
                        .service("/health", HealthCheckService.builder().build())
                        .service("/",
                                (ctx, req) -> {
                                    CloseableHttpResponse response = backendClient.execute(
                                            new HttpGet(System.getProperty("backend.endpoint", "http://127.0.0.1:9000/api")));
                                    try (InputStream is = response.getEntity().getContent()) {
                                        String text = new BufferedReader(
                                                new InputStreamReader(is, StandardCharsets.UTF_8))
                                                .lines()
                                                .collect(Collectors.joining("\n"));
                                        return HttpResponse.of(HttpStatus.valueOf(response.getCode()),
                                                MediaType.parse(response.getFirstHeader("Content-type").getValue()),
                                                text);
                                    }
                                })
                        .decorator(BraveService.newDecorator(httpTracing))
                        .decorator(LoggingService.newDecorator())
                        .build();

        server.start().join();
    }
}

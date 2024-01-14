package brave.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import brave.propagation.Propagation;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.client.eureka.EurekaEndpointGroup;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext;
import com.linecorp.armeria.common.logging.RequestLogLevelMapper;
import java.io.IOException;
import zipkin2.reporter.BytesMessageSender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;

final class HttpTracingFactory {
  static final Logger LOGGER = LoggerFactory.getLogger(HttpTracingFactory.class);
  static final BaggageField USER_NAME = BaggageField.create("userName");

  /** Decides how to name and tag spans. By default, they are named the same as the http method. */
  static HttpTracing create(String serviceName) {
    return HttpTracing.create(tracing((System.getProperty("brave.localServiceName", serviceName))));
  }

  /** Controls aspects of tracing such as the service name that shows up in the UI */
  static Tracing tracing(String serviceName) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .supportsJoin(Boolean.parseBoolean(System.getProperty("brave.supportsJoin", "true")))
        .supportsJoin(Boolean.parseBoolean(System.getProperty("brave.traceId128Bit", "false")))
        .propagationFactory(propagationFactory())
        .currentTraceContext(currentTraceContext(correlationScopeDecorator()))
        .addSpanHandler(spanHandler(sender()))
        .build();
  }

  /** Allows log patterns to use {@code %{traceId}} {@code %{spanId}} and {@code %{userName}} */
  static ScopeDecorator correlationScopeDecorator() {
    return MDCScopeDecorator.newBuilder()
        .add(CorrelationScopeConfig.SingleCorrelationField.create(USER_NAME)).build();
  }

  /** Propagates trace context between threads. */
  static CurrentTraceContext currentTraceContext(ScopeDecorator correlationScopeDecorator) {
    return RequestContextCurrentTraceContext.builder()
        .addScopeDecorator(correlationScopeDecorator)
        .build();
  }

  /** Configures propagation for {@link #USER_NAME}, using the remote header "user_name" */
  static Propagation.Factory propagationFactory() {
    return BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
        .add(BaggagePropagationConfig.SingleBaggageField.newBuilder(USER_NAME)
            .addKeyName("user_name")
            .build())
        .build();
  }

  /** Configuration for how to send spans to Zipkin */
  static BytesMessageSender sender() {
    String postPath = "/api/v2/spans";
    String eurekaUri = System.getenv("EUREKA_SERVICE_URL");
    if (eurekaUri != null && !eurekaUri.isEmpty()) {
      EurekaEndpointGroup zipkin =
          EurekaEndpointGroup.builder(eurekaUri).appName("zipkin").build();
      LOGGER.info("Using eureka to discover zipkin: {}", eurekaUri);
      Runtime.getRuntime().addShutdownHook(new Thread(zipkin::close));
      return new WebClientSender(WebClient.of(SessionProtocol.H2C, zipkin, postPath));
    }
    String zipkinUri =
        System.getProperty("zipkin.baseUrl", "http://127.0.0.1:9411") + postPath;
    LOGGER.info("Using zipkin URI: {}", zipkinUri);
    return new WebClientSender(WebClient.of(zipkinUri));
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  static AsyncZipkinSpanHandler spanHandler(BytesMessageSender sender) {
    final AsyncZipkinSpanHandler spanHandler = AsyncZipkinSpanHandler.create(sender);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      spanHandler.close(); // Make sure spans are reported on shutdown
      try {
        sender.close(); // Release any network resources used to send spans
      } catch (IOException e) {
        LOGGER.warn("error closing trace sender: " + e.getMessage());
      }
    }));

    return spanHandler;
  }

  private HttpTracingFactory() {
  }
}

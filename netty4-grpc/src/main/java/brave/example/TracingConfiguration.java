package brave.example;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.grpc.GrpcTracing;
import brave.http.HttpTracing;
import brave.netty.http.NettyHttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.rpc.RpcTracing;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.netty.channel.ChannelHandler;
import java.io.IOException;
import java.util.logging.Logger;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

final class TracingConfiguration {
  final HttpTracing httpTracing;
  final RpcTracing rpcTracing;

  static TracingConfiguration create(String defaultServiceName) {
    Tracing tracing = TracingFactory.create(defaultServiceName);
    return new TracingConfiguration(HttpTracing.create(tracing), RpcTracing.create(tracing));
  }

  TracingConfiguration(HttpTracing httpTracing, RpcTracing rpcTracing) {
    this.httpTracing = httpTracing;
    this.rpcTracing = rpcTracing;
  }

  public ChannelHandler serverHandler() {
    return NettyHttpTracing.create(httpTracing).serverHandler();
  }

  public ClientInterceptor clientInterceptor() {
    // TODO: RpcTracing.clientOf("backend") equivalent
    return GrpcTracing.create(rpcTracing).newClientInterceptor();
  }

  public ServerInterceptor serverInterceptor() {
    return GrpcTracing.create(rpcTracing).newServerInterceptor();
  }

  static final class TracingFactory {
    static final BaggageField USER_NAME = BaggageField.create("userName");

    /** Decides how to name and tag spans. By default they are named the same as the http method. */
    static Tracing create(String serviceName) {
      return tracing(System.getProperty("brave.localServiceName", serviceName));
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
      return ThreadLocalCurrentTraceContext.newBuilder()
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
    static Sender sender() {
      return URLConnectionSender.create(
          System.getProperty("zipkin.baseUrl", "http://127.0.0.1:9411") + "/api/v2/spans");
    }

    /** Configuration for how to buffer spans into messages for Zipkin */
    static AsyncZipkinSpanHandler spanHandler(Sender sender) {
      final AsyncZipkinSpanHandler spanHandler = AsyncZipkinSpanHandler.create(sender);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        spanHandler.close(); // Make sure spans are reported on shutdown
        try {
          sender.close(); // Release any network resources used to send spans
        } catch (IOException e) {
          Logger.getAnonymousLogger().warning("error closing trace sender: " + e.getMessage());
        }
      }));

      return spanHandler;
    }

    private TracingFactory() {
    }
  }
}

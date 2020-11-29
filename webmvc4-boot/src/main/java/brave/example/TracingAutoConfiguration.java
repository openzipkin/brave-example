package brave.example;

import brave.CurrentSpanCustomizer;
import brave.SpanCustomizer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig.SingleBaggageField;
import brave.baggage.CorrelationScopeConfig.SingleCorrelationField;
import brave.context.slf4j.MDCScopeDecorator;
import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.servlet.TracingFilter;
import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import javax.servlet.Filter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

/** This adds tracing configuration to any web mvc controllers or rest template clients. */
@Configuration
// Importing a class is effectively the same as declaring bean methods
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class TracingAutoConfiguration {
  static final BaggageField USER_NAME = BaggageField.create("userName");

  /** Allows log patterns to use {@code %{traceId}} {@code %{spanId}} and {@code %{userName}} */
  @Bean ScopeDecorator correlationScopeDecorator() {
    return MDCScopeDecorator.newBuilder()
        .add(SingleCorrelationField.create(USER_NAME)).build();
  }

  /** Propagates trace context between threads. */
  @Bean CurrentTraceContext currentTraceContext(ScopeDecorator correlationScopeDecorator) {
    return ThreadLocalCurrentTraceContext.newBuilder()
        .addScopeDecorator(correlationScopeDecorator)
        .build();
  }

  /** Configures propagation for {@link #USER_NAME}, using the remote header "user_name" */
  @Bean Propagation.Factory propagationFactory() {
    return BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
        .add(SingleBaggageField.newBuilder(USER_NAME).addKeyName("user_name").build())
        .build();
  }

  /** Configuration for how to send spans to Zipkin */
  @Bean Sender sender(
      @Value("${zipkin.baseUrl:http://127.0.0.1:9411}/api/v2/spans") String zipkinEndpoint) {
    return OkHttpSender.create(zipkinEndpoint);
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean AsyncZipkinSpanHandler zipkinSpanHandler(Sender sender) {
    return AsyncZipkinSpanHandler.create(sender);
  }

  /** Controls aspects of tracing such as the service name that shows up in the UI */
  @Bean Tracing tracing(
      @Value("${brave.localServiceName:${spring.application.name}}") String serviceName,
      @Value("${brave.supportsJoin:true}") boolean supportsJoin,
      @Value("${brave.traceId128Bit:false}") boolean traceId128Bit,
      Propagation.Factory propagationFactory,
      CurrentTraceContext currentTraceContext,
      AsyncZipkinSpanHandler zipkinSpanHandler) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .supportsJoin(supportsJoin)
        .traceId128Bit(traceId128Bit)
        .propagationFactory(propagationFactory)
        .currentTraceContext(currentTraceContext)
        .addSpanHandler(zipkinSpanHandler).build();
  }

  /** Allows someone to add tags to a span if a trace is in progress. */
  @Bean SpanCustomizer spanCustomizer(Tracing tracing) {
    return CurrentSpanCustomizer.create(tracing);
  }

  /** Decides how to name and tag spans. By default they are named the same as the http method. */
  @Bean HttpTracing httpTracing(Tracing tracing) {
    return HttpTracing.create(tracing);
  }

  /** Creates server spans for HTTP requests */
  @Bean Filter tracingFilter(HttpTracing httpTracing) {
    return TracingFilter.create(httpTracing);
  }

  /** Adds MVC Controller tags to server spans */
  @Bean WebMvcConfigurer tracingWebMvcConfigurer(
      final SpanCustomizingAsyncHandlerInterceptor webMvcTracingCustomizer) {
    return new WebMvcConfigurerAdapter() {
      /** Adds application-defined web controller details to HTTP server spans */
      @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webMvcTracingCustomizer);
      }
    };
  }

  /**
   * Creates client spans for HTTP requests.
   *
   * <p>{@link OkHttpClient} because {@link OkHttp3ClientHttpRequestFactory} doesn't take a
   * {@link Call.Factory}
   */
  @Bean OkHttpClient tracedOkHttpClient(HttpTracing httpTracing) {
    return new OkHttpClient.Builder()
        .addNetworkInterceptor(TracingInterceptor.create(httpTracing))
        .build();
  }
}

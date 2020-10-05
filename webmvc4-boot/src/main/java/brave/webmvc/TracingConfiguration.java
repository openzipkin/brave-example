package brave.webmvc;

import brave.CurrentSpanCustomizer;
import brave.SpanCustomizer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig.SingleBaggageField;
import brave.baggage.CorrelationScopeConfig.SingleCorrelationField;
import brave.context.slf4j.MDCScopeDecorator;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.servlet.TracingFilter;
import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import javax.servlet.Filter;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * This adds tracing configuration to any web mvc controllers or rest template clients.
 */
@Configuration
// Importing a class is effectively the same as declaring bean methods
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class TracingConfiguration {
  static final BaggageField USER_NAME = BaggageField.create("userName");

  /** Allows log patterns to use {@code %{traceId}} {@code %{spanId}} and {@code %{userName}} */
  @Bean ScopeDecorator correlationScopeDecorator() {
    return MDCScopeDecorator.newBuilder()
        .add(SingleCorrelationField.create(USER_NAME)).build();
  }

  /** Configures propagation for {@link #USER_NAME}, using the remote header "user_name" */
  @Bean Propagation.Factory propagationFactory() {
    return BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
        .add(SingleBaggageField.newBuilder(USER_NAME).addKeyName("user_name").build())
        .build();
  }

  /** Configuration for how to send spans to Zipkin */
  @Bean Sender sender(
      @Value("${zipkin.endpoint:http://127.0.0.1:9411/api/v2/spans}") String zipkinEndpoint) {
    return OkHttpSender.create(zipkinEndpoint);
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean AsyncZipkinSpanHandler zipkinSpanHandler(Sender sender) {
    return AsyncZipkinSpanHandler.create(sender);
  }

  /** Controls aspects of tracing such as the service name that shows up in the UI */
  @Bean Tracing tracing(
      @Value("${spring.application.name:brave-webmvc-example}") String serviceName,
      Propagation.Factory propagationFactory,
      ScopeDecorator correlationScopeDecorator,
      AsyncZipkinSpanHandler zipkinSpanHandler) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .propagationFactory(propagationFactory)
        .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
            .addScopeDecorator(correlationScopeDecorator)
            .build()
        )
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

  /** Creates server spans for http requests */
  @Bean Filter tracingFilter(HttpTracing httpTracing) {
    return TracingFilter.create(httpTracing);
  }

  @Bean RestTemplateCustomizer useTracedHttpClient(HttpTracing httpTracing) {
    final CloseableHttpClient httpClient = TracingHttpClientBuilder.create(httpTracing).build();
    return new RestTemplateCustomizer() {
      @Override public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
      }
    };
  }

  @Bean WebMvcConfigurer tracingWebMvcConfigurer(
      final SpanCustomizingAsyncHandlerInterceptor webMvcTracingCustomizer) {
    return new WebMvcConfigurerAdapter() {
      /** Decorates server spans with application-defined web tags */
      @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webMvcTracingCustomizer);
      }
    };
  }
}

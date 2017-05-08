package brave.webmvc;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.servlet.TracingFilter;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * This adds tracing configuration to any web mvc controllers or rest template clients. This should
 * be configured last.
 */
@Configuration
// import as the interceptors are annotation with javax.inject and not automatically wired
@Import({TracingClientHttpRequestInterceptor.class, TracingHandlerInterceptor.class})
public class TracingConfiguration extends WebMvcConfigurerAdapter {

  /** Configuration for how to send spans to Zipkin */
  @Bean Sender sender() {
    return OkHttpSender.create("http://127.0.0.1:9411/api/v1/spans");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean Reporter<Span> reporter() {
    return AsyncReporter.builder(sender()).build();
  }

  /** Controls aspects of tracing such as the name that shows up in the UI */
  @Bean Tracing tracing() {
    return Tracing.newBuilder()
        .localServiceName("brave-webmvc-example")
        .currentTraceContext(ThreadContextCurrentTraceContext.create()) // puts trace IDs into logs
        .reporter(reporter()).build();
  }

  // decides how to name and tag spans. By default they are named the same as the http method.
  @Bean HttpTracing httpTracing() {
    return HttpTracing.create(tracing());
  }

  @Bean ClientHttpRequestFactory tracingFactory(HttpTracing httpTracing) {
    return new HttpComponentsClientHttpRequestFactory(
        TracingHttpClientBuilder.create(httpTracing).build()
    );
  }

  @Bean Filter tracingFilter(HttpTracing httpTracing) {
    return TracingFilter.create(httpTracing);
  }

  @Autowired
  private TracingHandlerInterceptor serverInterceptor;

  @Autowired
  private TracingClientHttpRequestInterceptor clientInterceptor;

  @Autowired
  private ClientHttpRequestFactory tracingFactory;

  @Autowired
  private RestTemplate restTemplate;

  /** adds tracing to the {@linkplain ExampleController application-defined} rest template */
  @PostConstruct public void init() {
    List<ClientHttpRequestInterceptor> interceptors =
        new ArrayList<>(restTemplate.getInterceptors());
    interceptors.add(clientInterceptor);
    restTemplate.setInterceptors(interceptors);
    restTemplate.setRequestFactory(tracingFactory);
  }

  /** adds tracing to the {@linkplain ExampleController application-defined} web controller */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(serverInterceptor);
  }
}

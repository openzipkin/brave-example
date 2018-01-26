package brave.webmvc;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * This adds tracing configuration to any web mvc controllers or rest template clients. This should
 * be configured last.
 */
@Configuration
// Importing these classes is effectively the same as declaring bean methods
@Import({TracingClientHttpRequestInterceptor.class, TracingHandlerInterceptor.class})
public class TracingConfiguration extends WebMvcConfigurerAdapter {

  /** Configuration for how to send spans to Zipkin */
  @Bean Sender sender() {
    return OkHttpSender.create("http://192.168.99.100:9411/api/v2/spans");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean AsyncReporter<Span> spanReporter() {
    return AsyncReporter.create(sender());
  }

  /** Controls aspects of tracing such as the name that shows up in the UI */
  @Bean Tracing tracing(@Value("${zipkin.service:brave-webmvc-example}") String serviceName) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
        .currentTraceContext(ThreadContextCurrentTraceContext.create()) // puts trace IDs into logs
        .spanReporter(spanReporter()).build();
  }

  // decides how to name and tag spans. By default they are named the same as the http method.
  @Bean HttpTracing httpTracing(Tracing tracing) {
    return HttpTracing.create(tracing);
  }

  @Autowired
  private TracingHandlerInterceptor serverInterceptor;

  @Autowired
  private TracingClientHttpRequestInterceptor clientInterceptor;

  @Autowired
  private RestTemplate restTemplate;

  /** adds tracing to the application-defined rest template */
  @PostConstruct public void init() {
    List<ClientHttpRequestInterceptor> interceptors =
        new ArrayList<>(restTemplate.getInterceptors());
    interceptors.add(clientInterceptor);
    restTemplate.setInterceptors(interceptors);
  }

  /** adds tracing to the application-defined web controller */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(serverInterceptor);
  }
}

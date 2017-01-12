package brave.webmvc;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.LoggingReporter;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import com.github.kristofa.brave.spring.ServletHandlerInterceptor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.Span;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * This adds tracing configuration to any web mvc controllers or rest template clients. This should
 * be configured last.
 */
@Configuration
// import as the interceptors are annotation with javax.inject and not automatically wired
@Import({BraveClientHttpRequestInterceptor.class, ServletHandlerInterceptor.class})
public class WebTracingConfiguration extends WebMvcConfigurerAdapter {

  /** Configuration for how to send spans to Zipkin */
  @Bean Sender sender() {
    return OkHttpSender.create("http://127.0.0.1:9411/api/v1/spans");
    //return LibthriftSender.create("127.0.0.1");
    // return KafkaSender.create("127.0.0.1:9092");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean Reporter<Span> reporter() {
    return new LoggingReporter();
    // uncomment to actually send to zipkin!
    //return zipkin.reporter.AsyncReporter.builder(sender()).build();
  }

  @Bean Brave brave() {
    return new Brave.Builder("brave-webmvc-example").reporter(reporter()).build();
  }

  // decide how to name spans. By default they are named the same as the http method.
  @Bean SpanNameProvider spanNameProvider() {
    return new DefaultSpanNameProvider();
  }

  @Autowired
  private ServletHandlerInterceptor serverInterceptor;

  @Autowired
  private BraveClientHttpRequestInterceptor clientInterceptor;

  @Autowired
  private RestTemplate restTemplate;

  // adds tracing to the application-defined rest template
  @PostConstruct
  public void init() {
    List<ClientHttpRequestInterceptor> interceptors =
        new ArrayList<ClientHttpRequestInterceptor>(restTemplate.getInterceptors());
    interceptors.add(clientInterceptor);
    restTemplate.setInterceptors(interceptors);
  }

  // adds tracing to the application-defined web controllers
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(serverInterceptor);
  }
}

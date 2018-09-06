package brave.webmvc;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.servlet.TracingFilter;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
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
 * This adds tracing configuration to any web mvc controllers or rest template clients.
 */
@Configuration
// Importing a class is effectively the same as declaring bean methods
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class TracingConfiguration extends WebMvcConfigurerAdapter {

  /** Configuration for how to send spans to Zipkin */
  @Bean Sender sender() {
    return OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean AsyncReporter<Span> spanReporter() {
    return AsyncReporter.create(sender());
  }

  /** Controls aspects of tracing such as the name that shows up in the UI */
  @Bean Tracing tracing(@Value("${spring.application.name}") String serviceName) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
        .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
            .addScopeDecorator(MDCScopeDecorator.create()) // puts trace IDs into logs
            .build()
        )
        .spanReporter(spanReporter()).build();
  }

  /** decides how to name and tag spans. By default they are named the same as the http method. */
  @Bean HttpTracing httpTracing(Tracing tracing) {
    return HttpTracing.create(tracing);
  }

  /** Creates client spans for http requests */
  // We are using a BPP as the Frontend supplies a RestTemplate bean prior to this configuration
  @Bean BeanPostProcessor connectionFactoryDecorator(final BeanFactory beanFactory) {
    return new BeanPostProcessor() {
      @Override public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
      }

      @Override public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (!(bean instanceof RestTemplate)) return bean;

        RestTemplate restTemplate = (RestTemplate) bean;
        List<ClientHttpRequestInterceptor> interceptors =
            new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(0, getTracingInterceptor());
        restTemplate.setInterceptors(interceptors);
        return bean;
      }

      // Lazy lookup so that the BPP doesn't end up needing to proxy anything.
      ClientHttpRequestInterceptor getTracingInterceptor() {
        return TracingClientHttpRequestInterceptor.create(beanFactory.getBean(HttpTracing.class));
      }
    };
  }

  /** Creates server spans for http requests */
  @Bean Filter tracingFilter(HttpTracing httpTracing) {
    return TracingFilter.create(httpTracing);
  }

  @Autowired SpanCustomizingAsyncHandlerInterceptor webMvcTracingCustomizer;

  /** Decorates server spans with application-defined web tags */
  @Override public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(webMvcTracingCustomizer);
  }
}

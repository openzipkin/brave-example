package brave.example;

import brave.http.HttpTracing;
import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;

/** This adds micrometer tracing features not yet configurable via properties. */
// This type makes support easier as forks can make a diff off a working type */
@Configuration
// Not autoconfiguration as for some reason this doesn't apply when added to spring.factories
public class ObservationConfiguration {

  /**
   * Sleuth used to have a property to skip HTTP patterns, but this hasn't been ported to the Spring
   * Boot 3 micrometer approach, yet.
   *
   * <p><pre>
   * spring.sleuth.web.additional-skip-pattern=/health
   * </pre>
   *
   * <p>Brave uses {@link HttpTracing#serverRequestSampler()} for server request sampling policy.
   * Micrometer tracing bridges to Brave's core API, so it doesn't see or use the HTTP, Messaging or
   * RPC policies. Instead, it relies on its own type, {@link ObservationPredicate}, which applies
   * both to metrics and tracing.
   *
   * <p>Spring boot 3.x implements properties for name based filtering. However, actuator endpoints
   * all use the same name "http.server.requests". This means we need to define our own
   * {@linkplain ObservationPredicate} to qualify this to a path. Unlike Brave, there is no generic
   * HTTP interface to get the path from. Instead, you need a type of context and also a library
   * specific carrier to get the path. Since we depend on "spring-boot-starter-webflux", we know
   * the request carrier is the reactive {@link ServerHttpRequest}, and the context to get that is
   * {@link ServerRequestObservationContext}.
   *
   * @see <a href="https://github.com/spring-projects/spring-boot/issues/34801">spring-boot issue 34801</a>
   */
  @Bean ObservationPredicate dontTraceHealth() {
    return (name, context) -> {
      if (name.equals("http.server.requests") &&
          context instanceof ServerRequestObservationContext serverContext) {
        return !serverContext.getCarrier().getPath().value().startsWith("/health");
      }
      return true;
    };
  }
}

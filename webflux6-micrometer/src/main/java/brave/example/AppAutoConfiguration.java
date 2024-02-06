package brave.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/** The application is simple, it only uses WebFlux. */
// This type makes support easier as forks can make a diff off a working AutoConfiguration type */
@Configuration
public class AppAutoConfiguration {
  @Bean WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}

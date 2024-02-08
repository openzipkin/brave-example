package brave.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** The application is simple, it only uses Web MVC and a {@linkplain RestTemplate}. */
// This type makes support easier as forks can make a diff off a working AutoConfiguration type */
@Configuration
public class AppAutoConfiguration {
  @Bean RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

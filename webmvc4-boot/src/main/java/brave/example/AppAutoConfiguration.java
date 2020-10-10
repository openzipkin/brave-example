package brave.example;

import okhttp3.OkHttpClient;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/** The application is simple, it only uses Web MVC and a {@linkplain RestTemplate}. */
@Configuration
public class AppAutoConfiguration {
  @Bean RestTemplateCustomizer useOkHttpClient(final OkHttpClient okHttpClient) {
    return new RestTemplateCustomizer() {
      @Override public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(okHttpClient));
      }
    };
  }
}

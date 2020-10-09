package brave.example;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** The application is simple, it only uses Web MVC and a {@linkplain RestTemplate}. */
@EnableWebMvc
public class AppConfiguration {
  @Autowired(required = false)
  OkHttpClient okHttpClient;

  @Bean RestTemplate restTemplate() {
    OkHttpClient okHttpClient = this.okHttpClient;
    if (okHttpClient == null) okHttpClient = new OkHttpClient.Builder().build();
    return new RestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient));
  }
}

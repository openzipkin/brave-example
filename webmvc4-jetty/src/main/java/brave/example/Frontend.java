package brave.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class Frontend {
  final RestTemplate restTemplate;
  final String backendEndpoint;

  @Autowired Frontend(RestTemplate restTemplate,
      @Value("${backend.endpoint:http://127.0.0.1:9000/api}") String backendEndpoint) {
    this.restTemplate = restTemplate;
    this.backendEndpoint = backendEndpoint;
  }

  @RequestMapping("/") public String callBackend() {
    return restTemplate.getForObject(backendEndpoint, String.class);
  }
}

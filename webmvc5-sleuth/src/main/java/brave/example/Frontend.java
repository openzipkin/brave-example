package brave.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static brave.example.Discovery.withDiscoveryArgs;

@EnableAutoConfiguration
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

  public static void main(String[] args) {
    SpringApplication.run(Frontend.class, withDiscoveryArgs(
        "--spring.application.name=frontend",
        "--server.port=8081")
    );
  }
}

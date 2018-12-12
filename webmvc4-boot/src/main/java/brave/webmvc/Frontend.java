package brave.webmvc;

import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@RestController
@CrossOrigin // So that javascript can be hosted elsewhere
public class Frontend {

  final AsyncRestTemplate restTemplate;

  @Autowired Frontend(AsyncClientHttpRequestFactory asyncClientHttpRequestFactory) {
    this.restTemplate = new AsyncRestTemplate(asyncClientHttpRequestFactory);
  }

  @RequestMapping("/") public String callBackend() throws ExecutionException, InterruptedException {
    ListenableFuture<ResponseEntity<String>>
        future1 = restTemplate.getForEntity("http://localhost:9000/api", String.class);
    ListenableFuture<ResponseEntity<String>>
        future2 = restTemplate.getForEntity("http://localhost:9000/api", String.class);
    future1.get();
    future2.get();
    return "foo";
  }

  public static void main(String[] args) {
    SpringApplication.run(Frontend.class,
        "--spring.application.name=frontend",
        "--server.port=8081"
    );
  }
}

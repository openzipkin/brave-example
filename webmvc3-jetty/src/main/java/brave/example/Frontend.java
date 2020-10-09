package brave.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class Frontend {
  final RestTemplate restTemplate;
  final String backendEndpoint;

  @Autowired Frontend(RestTemplate restTemplate, String backendEndpoint) {
    this.backendEndpoint = backendEndpoint;
    this.restTemplate = restTemplate;
  }

  @RequestMapping("/") public ResponseEntity<String> callBackend() {
    String result = restTemplate.getForObject(backendEndpoint, String.class);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }
}

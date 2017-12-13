package brave.webmvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class Frontend {
  @Autowired RestTemplate template;

  @RequestMapping("/")
  public ResponseEntity<String> callBackend() {
    String result = template.getForObject("http://localhost:9000/api", String.class);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }
}

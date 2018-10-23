package brave.webmvc;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Frontend {
  @Autowired JmsTemplate jmsTemplate;

  @RequestMapping("/") public ResponseEntity<String> callBackend() {
    jmsTemplate.convertAndSend("backend", new Date());
    return new ResponseEntity<String>("sent", HttpStatus.OK);
  }
}

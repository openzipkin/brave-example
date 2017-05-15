package brave.webmvc;

import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

// notice there is no tracing code in this class
@Controller
public class ExampleController {
  Logger logger = Logger.getLogger(ExampleController.class.getName());

  @Autowired RestTemplate template;

  @RequestMapping("/a")
  public String a(HttpServletRequest request) throws InterruptedException {
    logger.info("in /a"); // arbitrary log message to show integration works
    Random random = new Random();
    Thread.sleep(random.nextInt(1000));

    // make a relative request to the same process
    StringBuffer nextUrl = request.getRequestURL();
    nextUrl.deleteCharAt(nextUrl.length() - 1).append('b');
    return template.getForObject(nextUrl.toString(), String.class);
  }

  @RequestMapping("/b")
  public ResponseEntity<String> b() throws InterruptedException {
    logger.info("in /b"); // arbitrary log message to show integration works
    Random random = new Random();
    Thread.sleep(random.nextInt(1000));

    return new ResponseEntity<String>("b", HttpStatus.OK);
  }
}

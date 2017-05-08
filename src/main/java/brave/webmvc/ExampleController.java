package brave.webmvc;

import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

// notice there is no tracing code in this class
@RestController
@Configuration
@EnableWebMvc
public class ExampleController {
  Logger logger = LogManager.getLogger();

  @Bean RestTemplate template() {
    return new RestTemplate();
  }

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
  public String b() throws InterruptedException {
    logger.info("in /b"); // arbitrary log message to show integration works
    Random random = new Random();
    Thread.sleep(random.nextInt(1000));

    return "b";
  }
}

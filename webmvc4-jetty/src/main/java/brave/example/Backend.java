package brave.example;

import java.util.Date;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class Backend {
  @RequestMapping("/api")
  public String printDate(@RequestHeader(name = "user_name", required = false) String username) {
    if (username != null) {
      return new Date().toString() + " " + username;
    }
    return new Date().toString();
  }
}

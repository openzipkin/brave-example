package brave.example;

import java.time.LocalDate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static brave.example.Discovery.withDiscoveryArgs;

@EnableAutoConfiguration
@RestController
public class Backend {

  @RequestMapping("/api")
  public String printDate(@RequestHeader(name = "user_name", required = false) String username) {
    String date = LocalDate.now().toString();
    if (username != null) {
      return date + " " + username;
    }
    return date;
  }

  public static void main(String[] args) {
    SpringApplication.run(Backend.class, withDiscoveryArgs(
        "--spring.application.name=backend",
        "--server.port=9000")
    );
  }
}

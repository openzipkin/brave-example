package brave.webmvc;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
public class Backend {

  @Autowired Tracing tracing;

  @RequestMapping("/api")
  public String printDate(@RequestHeader(name = "user-name", required = false) String username) {
    ScopedSpan span = tracing.tracer().startScopedSpan("child-of-backend");
    try {
      for (int i=10;i<13;i++) {
       tracing.tracer().startScopedSpan(Integer.toHexString(i)).finish();
      }
    } finally {
      span.finish();
    }
    if (username != null) {
      return new Date().toString() + " " + username;
    }
    return new Date().toString();
  }

  public static void main(String[] args) {
    SpringApplication.run(Backend.class,
        "--spring.application.name=backend",
        "--server.port=9000"
    );
  }
}

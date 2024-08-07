package brave.example;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@EnableAutoConfiguration
@RestController
@Import(value = {
    CustomObservationConfiguration.class,
    ZipkinDiscoveryConfiguration.class
})
public class Backend {

  @GetMapping("/api")
  public Mono<String> printDate(@RequestHeader("user_name") Optional<String> username) {
    return Mono.fromSupplier(() -> {
      String date = LocalDate.now().toString();
      return username.map(u -> date + " " + u).orElse(date);
    });
  }

  public static void main(String[] args) {
    new SpringApplicationBuilder(Backend.class)
        .properties(ZipkinDiscoveryConfiguration.discoveryProperties())
        .run("--spring.application.name=backend", "--server.port=9000");
  }
}

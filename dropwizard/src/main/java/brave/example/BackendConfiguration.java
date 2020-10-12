package brave.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.zipkin.HttpZipkinFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import io.dropwizard.Configuration;

public class BackendConfiguration extends Configuration {
  final ZipkinFactory zipkin = new HttpZipkinFactory();

  @JsonProperty public ZipkinFactory getZipkin() {
    return zipkin;
  }
}

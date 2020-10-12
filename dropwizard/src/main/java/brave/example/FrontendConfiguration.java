package brave.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.zipkin.HttpZipkinFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;
import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;

public class FrontendConfiguration extends Configuration {
  static class BackendConfiguration extends ZipkinClientConfiguration {
    @NotNull String endpoint = "http://127.0.0.1:9000/api";

    BackendConfiguration() {
      setServiceName("backend");
    }

    @JsonProperty public String getEndpoint() {
      return endpoint;
    }

    @JsonProperty public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
    }
  }

  final ZipkinFactory zipkin = new HttpZipkinFactory();
  final BackendConfiguration backend = new BackendConfiguration();

  @JsonProperty public ZipkinFactory getZipkin() {
    return zipkin;
  }

  @JsonProperty public BackendConfiguration getBackend() {
    return backend;
  }
}
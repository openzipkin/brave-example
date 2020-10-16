package brave.example;

import brave.sampler.Sampler;
import ratpack.zipkin.ServerTracingModule;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public final class ZipkinConfig {
  String baseUrl = "http://127.0.0.1:9411";
  String service;
  boolean supportsJoin = true;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public boolean isSupportsJoin() {
    return supportsJoin;
  }

  public void setSupportsJoin(boolean supportsJoin) {
    this.supportsJoin = supportsJoin;
  }

  ServerTracingModule.Config toModuleConfig() {
    return new ServerTracingModule.Config()
        // TODO spanHandler
        .spanReporterV2(AsyncReporter.create(URLConnectionSender.create(baseUrl + "/api/v2/spans")))
        .serviceName(service)
        // TODO supportsJoin
        .sampler(Sampler.ALWAYS_SAMPLE);
  }
}

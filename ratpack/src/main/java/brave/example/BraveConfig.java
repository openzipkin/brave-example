package brave.example;

import brave.sampler.Sampler;
import ratpack.zipkin.ServerTracingModule;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public final class BraveConfig {
  String localServiceName;
  boolean supportsJoin = true;
  boolean traceId128Bit = false;
  ZipkinConfig zipkin = new ZipkinConfig();

  public String getLocalServiceName() {
    return localServiceName;
  }

  public void setLocalServiceName(String localServiceName) {
    this.localServiceName = localServiceName;
  }

  public boolean isSupportsJoin() {
    return supportsJoin;
  }

  public void setSupportsJoin(boolean supportsJoin) {
    this.supportsJoin = supportsJoin;
  }

  public boolean isTraceId128Bit() {
    return traceId128Bit;
  }

  public void setTraceId128Bit(boolean traceId128Bit) {
    this.traceId128Bit = traceId128Bit;
  }

  public ZipkinConfig getZipkin() {
    return zipkin;
  }

  public void setZipkin(ZipkinConfig zipkin) {
    this.zipkin = zipkin;
  }

  public static final class ZipkinConfig {
    String baseUrl = "http://127.0.0.1:9411";

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }
  }

  ServerTracingModule.Config toModuleConfig() {
    return new ServerTracingModule.Config()
        // TODO spanHandler
        .spanReporterV2(
            AsyncReporter.create(URLConnectionSender.create(zipkin.baseUrl + "/api/v2/spans")))
        .serviceName(localServiceName)
        // TODO supportsJoin traceId128Bit
        .sampler(Sampler.ALWAYS_SAMPLE);
  }
}
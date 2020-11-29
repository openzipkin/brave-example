package brave.example;

import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public final class ZipkinConfig {
  String baseUrl = "http://127.0.0.1:9411";

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  Reporter<Span> toSpanReporter() {
    AsyncReporter<Span> result =
        AsyncReporter.create(URLConnectionSender.create(baseUrl + "/api/v2/spans"));
    // Make sure spans are reported on shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(result::close));
    return result;
  }
}

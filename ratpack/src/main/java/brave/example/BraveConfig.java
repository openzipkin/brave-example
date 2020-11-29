package brave.example;

import brave.sampler.Sampler;
import ratpack.zipkin.ServerTracingModule;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

public final class BraveConfig {
  String localServiceName;
  boolean supportsJoin = true;
  boolean traceId128Bit = false;
  Reporter<Span> spanReporter;

  public BraveConfig setLocalServiceName(String localServiceName) {
    this.localServiceName = localServiceName;
    return this;
  }

  public BraveConfig setSupportsJoin(boolean supportsJoin) {
    this.supportsJoin = supportsJoin;
    return this;
  }

  public BraveConfig setTraceId128Bit(boolean traceId128Bit) {
    this.traceId128Bit = traceId128Bit;
    return this;
  }

  public BraveConfig setSpanReporter(Reporter<Span> spanReporter) {
    this.spanReporter = spanReporter;
    return this;
  }

  ServerTracingModule.Config toModuleConfig() {
    return new ServerTracingModule.Config()
        .serviceName(localServiceName)
        .sampler(Sampler.ALWAYS_SAMPLE)
        .spanReporterV2(spanReporter)
        // TODO: supportsJoin
        // TODO: traceId128Bit
        ;
  }
}
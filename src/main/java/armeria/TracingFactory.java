package armeria;

import com.linecorp.armeria.common.tracing.RequestContextCurrentTraceContext;

import brave.Tracing;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.urlconnection.URLConnectionSender;

final class TracingFactory {

  /** Controls aspects of tracing such as the name that shows up in the UI */
  static Tracing create(String serviceName) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .currentTraceContext(RequestContextCurrentTraceContext.DEFAULT)
        .spanReporter(spanReporter())
        .build();
  }

  /** Configuration for how to send spans to Zipkin */
  static Sender sender() {
    return URLConnectionSender.create("http://localhost:9411/api/v2/spans");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  static AsyncReporter<Span> spanReporter() {
    final AsyncReporter<Span> result = AsyncReporter.create(sender());
    // make sure spans are reported on shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(result::close));
    return result;
  }

  private TracingFactory() {}
}

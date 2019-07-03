package armeria;

import java.io.IOException;
import java.util.logging.Logger;

import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext;

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
        .currentTraceContext(RequestContextCurrentTraceContext.ofDefault())
        .spanReporter(spanReporter(sender()))
        .build();
  }

  /** Configuration for how to send spans to Zipkin */
  static Sender sender() {
    return URLConnectionSender.create("http://localhost:9411/api/v2/spans");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  static AsyncReporter<Span> spanReporter(Sender sender) {
    final AsyncReporter<Span> spanReporter = AsyncReporter.create(sender);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      spanReporter.close(); // Make sure spans are reported on shutdown
      try {
        sender.close(); // Release any network resources used to send spans
      } catch (IOException e) {
        Logger.getAnonymousLogger().warning("error closing trace sender: " + e.getMessage());
      }
    }));

    return spanReporter;
  }

  private TracingFactory() {}
}

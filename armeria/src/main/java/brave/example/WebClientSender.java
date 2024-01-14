package brave.example;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.MediaType;
import java.io.IOException;
import java.util.List;
import zipkin2.reporter.BytesMessageEncoder;
import zipkin2.reporter.BytesMessageSender;
import zipkin2.reporter.ClosedSenderException;
import zipkin2.reporter.Encoding;

final class WebClientSender extends BytesMessageSender.Base {
  final WebClient zipkinApiV2SpansClient;
  volatile boolean closeCalled; // volatile as not called from the reporting thread.

  WebClientSender(WebClient zipkinApiV2SpansClient) {
    super(Encoding.JSON);
    this.zipkinApiV2SpansClient = zipkinApiV2SpansClient;
  }

  @Override public int messageMaxBytes() {
    return 500_000; // Use the most common HTTP default
  }

  @Override public void send(List<byte[]> encodedSpans) throws IOException {
    if (closeCalled) throw new ClosedSenderException();
    byte[] body = BytesMessageEncoder.JSON.encode(encodedSpans);
    HttpRequest request =
        HttpRequest.of(HttpMethod.POST, "", MediaType.JSON, HttpData.wrap(body));
    AggregatedHttpResponse response = zipkinApiV2SpansClient.blocking().execute(request);
    try (HttpData content = response.content()) {
      if (!response.status().isSuccess()) {
        if (content.isEmpty()) {
          throw new IOException("response failed: " + response);
        }
        throw new IOException("response failed: " + content.toStringAscii());
      }
    }
  }

  @Override public void close() {
    closeCalled = true;
  }
}

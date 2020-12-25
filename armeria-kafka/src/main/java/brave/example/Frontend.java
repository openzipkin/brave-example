package brave.example;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.LoggingService;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public final class Frontend {

  public static void main(String[] args) {
    TracingConfiguration tracing = TracingConfiguration.create("frontend", true);

    Properties configs = new Properties();
    String kafkaBootstrapServers = System.getProperty("kafka.bootstrap-servers", "localhost:19092");
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);

    StringSerializer keySerializer = new StringSerializer();
    StringSerializer valueSerializer = new StringSerializer();
    Producer<String, String> producer =
        tracing.kafkaTracing.producer(new KafkaProducer<>(configs, keySerializer, valueSerializer));

    Server server = Server.builder()
        .http(8081)
        .service("/health", HealthCheckService.of())
        .service("/", (ctx, req) -> {
          producer.send(new ProducerRecord<>("input", "hello", "world"));
          return HttpResponse.of(202);
        })
        .decorator(BraveService.newDecorator(tracing.httpTracing))
        .decorator(LoggingService.newDecorator())
        .build();

    server.start().join();
  }
}

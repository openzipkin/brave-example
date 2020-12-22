package brave.example;

import brave.http.HttpTracing;
import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingTracing;
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
    String kafkaBootstrapServers = System.getProperty("kafka.bootstrap-servers", "localhost:19092");

    final MessagingTracing messagingTracing = TracingFactory.createMessaging("producer-service");
    final KafkaTracing kafkaTracing = KafkaTracing.newBuilder(messagingTracing).build();

    final Properties configs = new Properties();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);

    final StringSerializer keySerializer = new StringSerializer();
    final StringSerializer valueSerializer = new StringSerializer();
    final Producer<String, String> producer = kafkaTracing.producer(new KafkaProducer<>(configs, keySerializer, valueSerializer));

    final HttpTracing httpTracing = TracingFactory.createHttp("frontend");

    final Server server =
        Server.builder()
            .http(8081)
            .service("/health", HealthCheckService.builder().build())
            .service("/", (ctx, req) -> {
              producer.send(new ProducerRecord<>("input", "hello", "world"));
              return HttpResponse.of(200);
            })
            .decorator(BraveService.newDecorator(httpTracing))
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }
}

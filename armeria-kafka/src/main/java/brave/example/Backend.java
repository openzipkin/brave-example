package brave.example;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import brave.kafka.streams.KafkaStreamsTracing;
import brave.messaging.MessagingTracing;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.LoggingService;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

public final class Backend {

  public static void main(String[] args) {
    String kafkaBootstrapServers = System.getProperty("kafka.bootstrap-servers", "localhost:19092");

    final Tracing tracing =
        TracingFactory.tracing(System.getProperty("brave.localServiceName", "backend"), false);
    final MessagingTracing msgTracing = TracingFactory.createMessaging(tracing);
    final KafkaTracing kafkaTracing = KafkaTracing.newBuilder(msgTracing).build();
    final KafkaStreamsTracing kafkaStreamsTracing = KafkaStreamsTracing.newBuilder(msgTracing)
        .build();

    final Properties streamsConfig = new Properties();
    streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "processor-service");
    streamsConfig
        .put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "earliest");

    final StreamsBuilder builder = new StreamsBuilder();
    builder.stream("input", Consumed.with(Serdes.String(), Serdes.String()))
        .transformValues(kafkaStreamsTracing.mapValues("mapping", s -> s + ". Thanks!"))
        .to("output", Produced.with(Serdes.String(), Serdes.String()));

    final KafkaStreams kafkaStreams = kafkaStreamsTracing
        .kafkaStreams(builder.build(), streamsConfig);

    kafkaStreams.start();

    final Properties consumerConfigs = new Properties();
    consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-service");
    consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    final StringDeserializer keyDeserializer = new StringDeserializer();
    final StringDeserializer valueDeserializer = new StringDeserializer();
    final Consumer<String, String> consumer = kafkaTracing
        .consumer(new KafkaConsumer<>(consumerConfigs, keyDeserializer, valueDeserializer));

    final ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.submit(new ConsumerLoop(consumer, Collections.singleton("output")));

    final Server server =
        Server.builder()
            .http(9000)
            .service("/health", HealthCheckService.builder().build())
            .decorator(LoggingService.newDecorator())
            .build();

    server.start().join();
  }

  static class ConsumerLoop implements Runnable {

    final Consumer<String, String> consumer;
    final Collection<String> topics;

    ConsumerLoop(Consumer<String, String> consumer, Collection<String> topics) {
      this.consumer = consumer;
      this.topics = topics;
    }

    @Override
    public void run() {
      try {
        consumer.subscribe(topics);
        while (!Thread.interrupted()) {
          for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMinutes(1))) {
            System.out.println("Record: " + record.key() + " " + record.value());
          }
          consumer.commitAsync();
        }
      } catch (WakeupException e) {
        // ignore for shutdown
      } finally {
        consumer.close();
      }
    }
  }
}

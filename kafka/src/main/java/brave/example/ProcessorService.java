package brave.example;

import brave.kafka.streams.KafkaStreamsTracing;
import brave.messaging.MessagingTracing;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

public final class ProcessorService {

  public static void main(String[] args) {
    final MessagingTracing messagingTracing = MessagingTracingFactory.create("processor-service");
    final KafkaStreamsTracing kafkaStreamsTracing = KafkaStreamsTracing.newBuilder(messagingTracing).build();

    final Properties configs = new Properties();
    configs.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
    configs.put(StreamsConfig.APPLICATION_ID_CONFIG, "processor-service");

    final StreamsBuilder builder = new StreamsBuilder();
    builder.stream("input", Consumed.with(Serdes.String(), Serdes.String()))
            .transformValues(kafkaStreamsTracing.mapValues("mapping", s -> s + ". Thanks!"))
            .to("output", Produced.with(Serdes.String(), Serdes.String()));

    final KafkaStreams kafkaStreams = kafkaStreamsTracing.kafkaStreams(builder.build(), configs);

    kafkaStreams.start();
  }
}

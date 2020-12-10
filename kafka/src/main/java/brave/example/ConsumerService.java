package brave.example;

import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingTracing;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public final class ConsumerService {

  public static void main(String[] args) {
    final MessagingTracing msgTracing = MessagingTracingFactory.create("consumer-service");
    final KafkaTracing kafkaTracing = KafkaTracing.newBuilder(msgTracing).build();

    final Properties configs = new Properties();
    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
    configs.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-service");
    configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    final StringDeserializer keyDeserializer = new StringDeserializer();
    final StringDeserializer valueDeserializer = new StringDeserializer();
    final Consumer<String, String> consumer = kafkaTracing.consumer(new KafkaConsumer<>(configs, keyDeserializer, valueDeserializer));

    consumer.subscribe(Collections.singleton("output"));
    while (!Thread.interrupted()) {
      for (ConsumerRecord<String, String> record: consumer.poll(Duration.ofMinutes(1))) {
        System.out.println("Record: " + record.key() + " " + record.value());
      }
      consumer.commitAsync();
    }
  }
}

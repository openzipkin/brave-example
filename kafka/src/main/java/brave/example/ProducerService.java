package brave.example;

import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingTracing;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public final class ProducerService {

  public static void main(String[] args) {
    final MessagingTracing messagingTracing = MessagingTracingFactory.create("producer-service");
    final KafkaTracing kafkaTracing = KafkaTracing.newBuilder(messagingTracing).build();

    final Properties configs = new Properties();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");

    final StringSerializer keySerializer = new StringSerializer();
    final StringSerializer valueSerializer = new StringSerializer();
    final Producer<String, String> producer = kafkaTracing.producer(new KafkaProducer<>(configs, keySerializer, valueSerializer));

    producer.send(new ProducerRecord<>("input", "hello", "world"));

    producer.close();
    messagingTracing.close();
  }
}

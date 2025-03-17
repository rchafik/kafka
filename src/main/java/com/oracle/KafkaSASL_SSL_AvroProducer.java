package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.oracle.avro.User;

import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import io.apicurio.registry.serde.config.SerdeConfig;

public class KafkaSASL_SSL_AvroProducer {

   public static void main(String[] args) throws Exception{

      String topicName = "users";

      Properties properties = new Properties();
      properties.put("bootstrap.servers", "bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092");
      properties.put("security.protocol", "SASL_SSL");
      properties.put("sasl.mechanism", "SCRAM-SHA-512");
      properties.put("ssl.truststore.location", "/home/opc/kafka/truststore.jks");
      properties.put("ssl.truststore.password", "ateam");
      properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"super-user-btaxq3z9d0ziwk0g\" password=\"719e35e5-017c-4cce-bb58-d8ed4f71201e\";");

      String registryUrl = "http://localhost:8080/apis/registry/v3";

      properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());
      properties.put(SerdeConfig.REGISTRY_URL, registryUrl);
      properties.put(SerdeConfig.AUTO_REGISTER_ARTIFACT, Boolean.TRUE);
      
      KafkaProducer<String, User> producer = new KafkaProducer<String, User>(properties);

      User user = new User(1, "Chafik", "chafik@example.com");

      ProducerRecord<String, User> record = new ProducerRecord<>(topicName, "user-key", user);

      producer.send(record, (metadata, exception) -> {
          if (exception == null) {
              System.out.println("Mensagem enviada para tópico: " + metadata.topic());
          } else {
              exception.printStackTrace();
          }
      });

      producer.close();

   }
}

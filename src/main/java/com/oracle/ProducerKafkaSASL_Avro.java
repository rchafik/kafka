package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class ProducerKafkaSASL_Avro {

   public static void main(String[] args) throws Exception{

      String topicName = "users";

      Properties properties = new Properties();
      properties.put("bootstrap.servers", "bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092");
      properties.put("security.protocol", "SASL_SSL");
      properties.put("sasl.mechanism", "SCRAM-SHA-512");
      properties.put("ssl.truststore.location", "/home/opc/kafka/truststore.jks");
      properties.put("ssl.truststore.password", "ateam");
      properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"super-user-btaxq3z9d0ziwk0g\" password=\"719e35e5-017c-4cce-bb58-d8ed4f71201e\";");
      properties.put("key.serializer", KafkaAvroSerializer.class.getName());
      properties.put("value.serializer", KafkaAvroSerializer.class.getName());      
      properties.put("schema.registry.url", "http://localhost:8080");
      
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

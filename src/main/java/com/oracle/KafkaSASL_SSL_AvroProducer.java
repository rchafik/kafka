package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.oracle.avro.User;
import com.oracle.util.Environments;
import com.oracle.util.PropertiesUtil;

import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import io.apicurio.registry.serde.config.SerdeConfig;

public class KafkaSASL_SSL_AvroProducer {

   public static void main(String[] args) throws Exception{

      String topicName = "users";
      Properties properties = PropertiesUtil.loadProperties(Environments.KAFKA_SASL_SSL_AVRO_PRODUCER);

      properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      //utilizando a classe para serializar do schema registry utilizado
      properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());

      //endpoint do schema registry
      String registryUrl = "http://localhost:8080/apis/registry/v3";
      properties.put(SerdeConfig.REGISTRY_URL, registryUrl);
      
      //auto-registrar o schema no schema registry
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

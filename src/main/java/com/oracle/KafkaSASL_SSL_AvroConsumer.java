package com.oracle;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.oracle.avro.User;
import com.oracle.util.Environments;
import com.oracle.util.PropertiesUtil;

import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import io.apicurio.registry.serde.config.SerdeConfig;

public class KafkaSASL_SSL_AvroConsumer {

   public static void main(String[] args) throws Exception {
       
      String topic = "users";

      Properties properties = PropertiesUtil.loadProperties(Environments.KAFKA_SASL_SSL_AVRO_CONSUMER);
      properties.put("key.deserializer", StringDeserializer.class.getName());
      //utilizando a classe para deserializar do schema registry utilizado
      properties.put("value.deserializer", AvroKafkaDeserializer.class.getName());
      
      String registryUrl = "http://localhost:8080/apis/registry/v3";
      properties.put(SerdeConfig.REGISTRY_URL, registryUrl);

      KafkaConsumer<String, User> consumer = new KafkaConsumer<String, User>(properties);

      consumer.subscribe(Arrays.asList(topic));
      System.out.println("Subscribed to topic " + topic);

      while (true) {
         ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, User> record : records)
               System.out.printf("offset = %d, key = %s, value = %s\n", 
               record.offset(), record.key(), record.value());
      }     
   }  
}

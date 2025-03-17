package com.oracle;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.oracle.avro.User;

import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import io.apicurio.registry.serde.config.SerdeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class KafkaSASL_SSL_AvroConsumer {

   public static void main(String[] args) throws Exception {
       
      String topic = "users";

      Properties props = new Properties();
      props.put("bootstrap.servers", "bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092");
      props.put("security.protocol", "SASL_SSL");
      props.put("sasl.mechanism", "SCRAM-SHA-512");
      props.put("ssl.truststore.location", "/home/opc/kafka/truststore.jks");
      props.put("ssl.truststore.password", "ateam");
      props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"super-user-btaxq3z9d0ziwk0g\" password=\"719e35e5-017c-4cce-bb58-d8ed4f71201e\";");
      props.put("group.id", "group-0");
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      props.put("session.timeout.ms", "30000");
      props.put("key.deserializer", StringDeserializer.class.getName());
      props.put("value.deserializer", AvroKafkaDeserializer.class.getName());
      
      String registryUrl = "http://localhost:8080/apis/registry/v3";
      props.put(SerdeConfig.REGISTRY_URL, registryUrl);
      props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

      KafkaConsumer<String, User> consumer = new KafkaConsumer<String, User>(props);

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

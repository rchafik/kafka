package com.oracle;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerKafkaSASL_SSL {

   public static void main(String[] args) throws Exception {
       
      String topic = "ateam-topic";

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
      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");



      KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

      consumer.subscribe(Arrays.asList(topic));
      System.out.println("Subscribed to topic " + topic);
      int i = 0;

      while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
               System.out.printf("offset = %d, key = %s, value = %s\n", 
               record.offset(), record.key(), record.value());
      }     
   }  
}

package com.oracle;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.oracle.util.Environments;
import com.oracle.util.PropertiesUtil;

public class KafkaMtlsConsumer {

   public static void main(String[] args) throws Exception {
       
      String topic = "mtls-topic";

      Properties properties = PropertiesUtil.loadProperties(Environments.KAFKA_MTLS_CONSUMER);

      KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

      consumer.subscribe(Arrays.asList(topic));
      System.out.println("Subscribed to topic " + topic);
      int i = 0;

      while (true) {
         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records)
               System.out.printf("offset = %d, key = %s, value = %s\n", 
               record.offset(), record.key(), record.value());
      }     
   }  
}

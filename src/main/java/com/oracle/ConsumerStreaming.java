package com.oracle;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerStreaming {

   public static void main(String[] args) throws Exception {
       
      String topic = "aTeamTopic";

      Properties props = new Properties();
      props.put("bootstrap.servers", "cell-1.streaming.sa-saopaulo-1.oci.oraclecloud.com:9092");
      props.put("security.protocol", "SASL_SSL");
      props.put("sasl.mechanism", "PLAIN");
      props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"latinoamerica/OracleIdentityCloudService/RODRIGO.CHOUEIRI@ORACLE.COM/ocid1.streampool.oc1.sa-saopaulo-1.amaaaaaafioir7iabhfoh367h6v5trqqynmmtcqfaxh2szoegpv3wdojkdza\" password=\">oGbEpIZ0-;sEr[EO;2m\";");
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

package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerStreaming {

   public static void main(String[] args) throws Exception{

      String topicName = "aTeamTopic";

      Properties properties = new Properties();
      properties.put("bootstrap.servers", "cell-1.streaming.sa-saopaulo-1.oci.oraclecloud.com:9092");
      properties.put("security.protocol", "SASL_SSL");
      properties.put("sasl.mechanism", "PLAIN");
      properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"latinoamerica/OracleIdentityCloudService/RODRIGO.CHOUEIRI@ORACLE.COM/ocid1.streampool.oc1.sa-saopaulo-1.amaaaaaafioir7iabhfoh367h6v5trqqynmmtcqfaxh2szoegpv3wdojkdza\" password=\">oGbEpIZ0-;sEr[EO;2m\";");
      properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

      KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

      for(int i = 0; i < 10; i++)
         producer.send(new ProducerRecord<String, String>(topicName, 
            Integer.toString(i), Integer.toString(i)));
               System.out.println("Message sent successfully");
               producer.close();
   }
}

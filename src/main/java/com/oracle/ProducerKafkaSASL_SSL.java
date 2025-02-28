package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerKafkaSASL_SSL {

   public static void main(String[] args) throws Exception{

      String topicName = "ateam-topic";

      Properties properties = new Properties();
      properties.put("bootstrap.servers", "bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092");
      properties.put("security.protocol", "SASL_SSL");
      properties.put("sasl.mechanism", "SCRAM-SHA-512");
      properties.put("ssl.truststore.location", "/home/opc/kafka/truststore.jks");
      properties.put("ssl.truststore.password", "ateam");
      properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"super-user-btaxq3z9d0ziwk0g\" password=\"719e35e5-017c-4cce-bb58-d8ed4f71201e\";");
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

package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerKafkaMTLS {

   public static void main(String[] args) throws Exception{

      String topicName = "ateam-topic";

      Properties properties = new Properties();
      properties.put("bootstrap.servers", "bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9093");
      properties.put("security.protocol", "SSL");
      properties.put("ssl.certificate.location", "/home/opc/kafka/leaf.cert");
      properties.put("ssl.key.location", "/home/opc/kafka/leaf.key");
      properties.put("ssl.keystore.location", "/home/opc/kafka/kafka-keystore.p12");
      properties.put("ssl.keystore.password", "ateam");
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

package com.oracle;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.oracle.util.Environments;
import com.oracle.util.PropertiesUtil;

public class KafkaMtlsProducer {

   public static void main(String[] args) throws Exception{

      String topicName = "mtls-topic";

      Properties properties = PropertiesUtil.loadProperties(Environments.KAFKA_MTLS_PRODUCER);

      KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

      for(int i = 0; i < 10; i++)
         producer.send(new ProducerRecord<String, String>(topicName, 
            Integer.toString(i), Integer.toString(i)));
               System.out.println("Message sent successfully");
               producer.close();
   }
}

# Streaming


# Kafka

**SSL properties**

```
security.protocol=SSL
ssl.certificate.location=/home/opc/kafka/leaf.cert
ssl.key.location=/home/opc/kafka/leaf.key
ssl.keystore.password=password
ssl.keystore.location=/home/opc/kafka/kafka-keystore.p12
```

**Comandos kafka com SSL**
```
kafka-topics.sh --create \
  --bootstrap-server bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9093 \
  --partitions 2 \
  --topic ateam-topic \
  --command-config /home/opc/kafka/kafkaclient.properties
  
kafka-console-producer.sh \
--broker-list bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9093 \
--topic ateam-topic --producer.config /home/opc/kafka/kafkaclient.properties

kafka-console-consumer.sh \
--bootstrap-server bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9093 \
 --topic ateam-topic --from-beginning --consumer.config /home/opc/kafka/kafkaclient.properties
```

**Habilitando o super user para ser utilizado com SASL_SSL**

```
oci kafka cluster enable-superuser --kafka-cluster-id ocid1.kafkacluster.oc1. --compartment-id ocid1.compartment.oc1. --secret-id ocid1.vaultsecret.oc1.
```

**SASL_SSL properties**

```
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
ssl.truststore.location=/home/opc/kafka/truststore.jks
ssl.truststore.password=password
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="superUserName" password="password";
```

**Comando kafka com SASL_SSL**

```
kafka-broker-api-versions.sh --bootstrap-server bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092 --command-config kafkasasl.properties
```

**Certificado para configurar truststore.jks**

https://cacerts.digicert.com/DigiCertGlobalRootG2.crt.pem?_gl=1*1c1f9jy*_gcl_au*MTk2Mjc0ODc1LjE3NDA2ODAzNTM.

**Geração da truststore.jks**

```
keytool -keystore truststore.jks -storepass password -alias oracle -import -file arquivo.pem
```

# Tasks
- [x] Produtor e Consumidor Streming
- [x] Produtor e Consumidor SASL-SCRAM
- [x] Produtor e Consumidor mTLS
- [ ] Rever boas práticas, como armazenar os artefatos de segurança em vault ou bucket

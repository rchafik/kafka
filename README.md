# Streaming


# Kafka


**Policy on behalf of Customer to Rawfka( MUST )**
```
Allow service rawfka to use vnics in compartment <compartment>
Allow service rawfka to use network-security-groups in compartment <compartment>
Allow service rawfka to use subnets in compartment <compartment>
```
 
**To enable SASL super user**
```
Allow service rawfka to {SECRET_UPDATE } in compartment <compartment>
Allow service rawfka to use secrets in compartment <compartment> where request.operation = 'UpdateSecret'
```

# Preparing all itens for Kafka setup

**generate a CA key**

openssl genpkey -algorithm RSA -out rootCA.key -aes256 -pass pass:yourpassword -pkeyopt rsa_keygen_bits:4096

**generate CA self signed cert:**

openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 3650 -out rootCA.pem -passin pass:yourpassword

**create leaf cert private key and csr(cert signed request):**

openssl genpkey -algorithm RSA -out leaf.key -pkeyopt rsa_keygen_bits:2048

**create leaf cert csr:**

openssl req -new -key leaf.key -out leaf.csr

**use root CA to sign leaf cert:**

openssl x509 -req -in leaf.csr -CA rootCA.pem -CAkey rootCA.key -CAcreateserial -out leaf.crt -days 825 -sha256 -passin pass:yourpassword

**Create kafka-keystore.p12 file, run the command below**

openssl pkcs12 -export -in leaf.crt -inkey leaf.key -out kafka-keystore.p12 -name kafka-key

**Certificado para configurar truststore.jks**

https://cacerts.digicert.com/DigiCertGlobalRootG2.crt.pem?_gl=1*1c1f9jy*_gcl_au*MTk2Mjc0ODc1LjE3NDA2ODAzNTM.

**Geração da truststore.jks**

```
keytool -keystore truststore.jks -storepass password -alias oracle -import -file arquivo.pem
```

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
**Usando Super User para criar novo usuário**
```
kafka-configs.sh --bootstrap-server bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092 \
  --alter --add-config "SCRAM-SHA-512=[password=ateam2025]" \
  --entity-type users --entity-name ateamUser \
  --command-config kafkasasl.properties
  
kafka-acls.sh --bootstrap-server bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092 \
  --add --allow-principal User:ateamUser \
  --operation Read --operation Write --operation Describe \
  --topic ateam-topic \
  --command-config kafkasasl.properties
  
kafka-acls.sh --bootstrap-server bootstrap-clstr-btaxq3z9d0ziwk0g.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092 \
  --add --allow-principal User:ateamUser \
  --operation Read --operation Describe --group group-0 \
  --command-config kafkasasl.properties

```
## Related Links
- [Manage Access Control Lists (ACLs) for Authorization in Confluent Platform](https://docs.confluent.io/platform/current/security/authorization/acls/manage-acls.html)

- [Authorization and ACLs - Kafka](https://kafka.apache.org/documentation/#security_authz)

- [User authentication and authorization in Apache Kafka](https://developer.ibm.com/tutorials/kafka-authn-authz/)

# Tasks
- [x] Produtor e Consumidor Streming
- [x] Produtor e Consumidor SASL-SCRAM
- [x] Produtor e Consumidor mTLS
- [ ] Remover conteúdo sensível, usando arquivo de propriedades
- [ ] Rever boas práticas, como armazenar os artefatos de segurança em vault ou bucket
- [x] Entender o que pode ser feito com o super user do Kafka
- [ ] Plugar uma interface gráfica para administrar o ambiente
- [ ] Testes com Mirror Maker



/*

This is a k6 test script that imports the xk6-kafka and
tests Kafka with a 200 JSON messages per iteration. It
also uses SASL authentication.

*/

import { check } from "k6";
import {
  Writer,
  Reader,
  Connection,
  SchemaRegistry,
  SCHEMA_TYPE_JSON,
  SASL_SCRAM_SHA512, //need to use this for OCI Kafka
  TLS_1_2,
} from "k6/x/kafka"; // import kafka extension

export const options = {
  // This is used for testing purposes. For real-world use, you should use your own options:
  // https://k6.io/docs/using-k6/k6-options/
  scenarios: {
    sasl_auth: {
      executor: "constant-vus",
      vus: 1,
      duration: "10s",
      gracefulStop: "1s",
    },
  },
};

const brokers = ["bootstrap-yyyy-xxxx.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092"]; // replace with your Kafka brokers and use SASL-SCRAM protocol
const topic = "xk6_kafka_json_topic";

// SASL config is optional
const saslConfig = {
  username: "super-user",
  password: "your super user password",
  // Possible values for the algorithm is:
  // NONE (default)
  // SASL_PLAIN
  // SASL_SCRAM_SHA256
  // SASL_SCRAM_SHA512
  // SASL_SSL (must enable TLS)
  // SASL_AWS_IAM (configurable via env or AWS IAM config files - no username/password needed)
  algorithm: SASL_SCRAM_SHA512, // OCI Kafka uses SCRAM-SHA512
};

// TLS config is optional
const tlsConfig = {
  // Enable/disable TLS (default: false)
  enableTls: true, // OCI Kafka uses TLS
  // Skip TLS verification if the certificate is invalid or self-signed (default: false)
  insecureSkipTlsVerify: true, // OCI Kafka uses self-signed certificates
  // Possible values:
  // TLS_1_0
  // TLS_1_1
  // TLS_1_2 (default)
  // TLS_1_3
  minVersion: TLS_1_2,

  // Enable client certificate authentication
   clientCertPem: "/home/opc/kafka/leaf.crt",
   clientKeyPem: "/home/opc/kafka/leaf.key",
   serverCaPem: "/home/opc/kafka/rootCA.pem",
};

const offset = 0;
// partition and groupId are mutually exclusive
const partition = 0;
const numPartitions = 1;
const replicationFactor = 1;

const writer = new Writer({
  brokers: brokers,
  topic: topic,
  sasl: saslConfig,
  tls: tlsConfig,
});
const reader = new Reader({
  brokers: brokers,
  topic: topic,
  partition: partition,
  offset: offset,
  sasl: saslConfig,
  tls: tlsConfig,
});
const connection = new Connection({
  address: brokers[0],
  sasl: saslConfig,
  tls: tlsConfig,
});
const schemaRegistry = new SchemaRegistry();

if (__VU == 0) {
  connection.createTopic({
    topic: topic,
    numPartitions: numPartitions,
    replicationFactor: replicationFactor,
  });
  console.log(
    "Existing topics: ",
    connection.listTopics(saslConfig, tlsConfig),
  );
}

export default function () {
  for (let index = 0; index < 100; index++) {
    let messages = [
      {
        key: schemaRegistry.serialize({
          data: {
            correlationId: "test-id-abc-" + index,
          },
          schemaType: SCHEMA_TYPE_JSON,
        }),
        value: schemaRegistry.serialize({
          data: {
            name: "xk6-kafka",
          },
          schemaType: SCHEMA_TYPE_JSON,
        }),
      },
      {
        key: schemaRegistry.serialize({
          data: {
            correlationId: "test-id-def-" + index,
          },
          schemaType: SCHEMA_TYPE_JSON,
        }),
        value: schemaRegistry.serialize({
          data: {
            name: "xk6-kafka",
          },
          schemaType: SCHEMA_TYPE_JSON,
        }),
      },
    ];

    writer.produce({ messages: messages });
  }

  // Read 10 messages only
  let messages = reader.consume({ limit: 10 });
  check(messages, {
    "10 messages returned": (msgs) => msgs.length == 10,
    "key is correct": (msgs) =>
      schemaRegistry
        .deserialize({ data: msgs[0].key, schemaType: SCHEMA_TYPE_JSON })
        .correlationId.startsWith("test-id-"),
    "value is correct": (msgs) =>
      schemaRegistry.deserialize({
        data: msgs[0].value,
        schemaType: SCHEMA_TYPE_JSON,
      }).name == "xk6-kafka",
  });
}

export function teardown(data) {
  if (__VU == 0) {
    // Delete the topic
    connection.deleteTopic(topic);
  }
  writer.close();
  reader.close();
  connection.close();
}

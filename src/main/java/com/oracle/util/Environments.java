package com.oracle.util;

public enum Environments {
    STREAMING_PRODUCER("streaming-producer"),
    STREAMING_CONSUMER("streaming-consumer"),
    KAFKA_MTLS_PRODUCER("kafka-mtls-producer"),
    KAFKA_MTLS_CONSUMER("kafka-mtls-consumer"),
    KAFKA_SASL_SSL_PRODUCER("kafka-sasl-ssl-producer"),
    KAFKA_SASL_SSL_CONSUMER("kafka-sasl-ssl-consumer");


    private final String environment;
 
    Environments(String environment) {
        this.environment = environment;
    }
 
    public String getEnvironment() {
        return this.environment;
    }

}

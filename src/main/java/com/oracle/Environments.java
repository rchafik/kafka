package com.oracle;

public enum Environments {
    STREAMING_PRODUCER("streaming-producer"),
    STREAMING_CONSUMER("streaming-consumer"),
    KAFKA_MTLS_PRODUCER("kafka-mtls-producer"),
    KAFKA_MTLS_CONSUMER("kafka-mtls-consumer");

    private final String environment;
 
    Environments(String environment) {
        this.environment = environment;
    }
 
    public String getEnvironment() {
        return this.environment;
    }

}

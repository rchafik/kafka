package com.oracle;

public enum Environments {
    STREAMING_PRODUCER("streaming-producer"),
    STREAMING_CONSUMER("streaming-consumer");

    private final String environment;
 
    Environments(String environment) {
        this.environment = environment;
    }
 
    public String getEnvironment() {
        return this.environment;
    }

}

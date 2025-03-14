package com.oracle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties loadProperties (Environments environment) {        
        Properties properties = null;
        try {
            InputStream input = new FileInputStream("/home/opc/projeto/kafka/config/" + environment.getEnvironment() + ".properties");
            properties = new java.util.Properties();
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load properties file: " + environment + ".properties");
        }
        return properties;
    }

    public static void main(String[] args) {
        Properties properties = PropertiesUtil.loadProperties(Environments.STREAMING_PRODUCER);
        System.out.println(properties.getProperty("bootstrap.servers"));
        System.out.println(properties.getProperty("sasl.jaas.config"));
    }
}

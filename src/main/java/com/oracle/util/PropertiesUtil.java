package com.oracle.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;

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

    public static String getSecretContent(String secretOcid) {
        String secret = null;

        try {
            final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
            final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            SecretsClient client = SecretsClient.builder().build(provider);

            GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder()
                    .secretId(secretOcid)
                    .stage(GetSecretBundleRequest.Stage.Latest).build();

            GetSecretBundleResponse response = client.getSecretBundle(getSecretBundleRequest);            
            Base64SecretBundleContentDetails secretBundleContentDetails = (Base64SecretBundleContentDetails)response.getSecretBundle().getSecretBundleContent();            
            String secretAsBase64 = secretBundleContentDetails.getContent();
            byte[] result = Base64.getDecoder().decode(secretAsBase64);
            secret = new String(result);            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load secret " + secretOcid);
        }

        return secret;
    }

    public static void writeJksFromSecret(String secretOcid) {
        try {
            final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
            final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            SecretsClient client = SecretsClient.builder().build(provider);

            GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder()
                    .secretId(secretOcid)
                    .stage(GetSecretBundleRequest.Stage.Latest).build();

            GetSecretBundleResponse response = client.getSecretBundle(getSecretBundleRequest);            
            Base64SecretBundleContentDetails secretBundleContentDetails = (Base64SecretBundleContentDetails)response.getSecretBundle().getSecretBundleContent();            
            String secretAsBase64 = secretBundleContentDetails.getContent();
            byte[] result = Base64.getDecoder().decode(secretAsBase64);

            try (FileOutputStream fos = new FileOutputStream("/home/opc/kafka/teste.jks")) {
                fos.write(result);
                System.out.println("Arquivo gerado com sucesso");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load secret " + secretOcid);
        }

    }    

    public static void main(String[] args) {
        writeJksFromSecret("ocid1.vaultsecret.oc1.sa-saopaulo-1.amaaaaaafioir7ia2blhuqd7f4og2u7g6c4a434vfw5qbibgudbtvlzdgqzq");
    }
}

package com.oracle.util;

import java.io.FileInputStream;
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

    public static String getSecret(String secretOcid) {
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

    public static void main(String[] args) {
        System.out.println("secret: " + getSecret("ocid1.vaultsecret.oc1.sa-saopaulo-1.amaaaaaafioir7iagrctybd6n75azpuvscz6u3jxbboi7gzvgecn2scx26oa"));
        System.out.println("secret: " + getSecret("ocid1.vaultsecret.oc1.sa-saopaulo-1.amaaaaaafioir7iakklh6uilaseuf3mynrgrrhzy4dljv226e3lcyjnjy32q"));
    }
}

package com.oracle;

import java.util.ArrayList;
import java.util.Arrays;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.PutMessagesDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetailsEntry;
import com.oracle.bmc.streaming.requests.PutMessagesRequest;
import com.oracle.bmc.streaming.responses.PutMessagesResponse;

/*
    Reference: https://docs.oracle.com/en-us/iaas/api/#/en/streaming/20180418/Message/PutMessages
    An example of how to put messages into a stream using the Oracle Cloud Infrastructure Streaming service with OCI Java SDK.
*/

public class PutMessagesExample {
    public static void main(String[] args) throws Exception {

        /**
         * Create a default authentication provider that uses the DEFAULT
         * profile in the configuration file.
         * Refer to <see href="https://docs.cloud.oracle.com/en-us/iaas/Content/API/Concepts/sdkconfig.htm#SDK_and_CLI_Configuration_File>the public documentation</see> on how to prepare a configuration file.
         */
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        final String ociMessageEndpoint = "https://hjabyoi4f6pinyo3ukvlwmcnri.apigateway.sa-saopaulo-1.oci.customer-oci.com";

        /* Create a service client */
        StreamClient client = StreamClient.builder().endpoint(ociMessageEndpoint).build(provider);

        /* Create a request and dependent object(s). */
        PutMessagesDetails putMessagesDetails = PutMessagesDetails.builder()
            .messages(new ArrayList<>(Arrays.asList(PutMessagesDetailsEntry.builder()
                    .key("Some byte data".getBytes())
                    .value("Some byte data".getBytes()).build()))).build();

        PutMessagesRequest putMessagesRequest = PutMessagesRequest.builder()
            .streamId("ocid1.stream.oc1.sa-saopaulo-1.amaaaaaafioir7iacg2i44q2qotiww2mpglsabswlu53wu2uz3rzpxzdpavq")
            .putMessagesDetails(putMessagesDetails)
            .opcRequestId("WLCZG53RQGRBV8GOAGD1<unique_ID>").build();

            /* Send request to the Client */
            PutMessagesResponse response = client.putMessages(putMessagesRequest);
            System.out.println("Response: " + response.get__httpStatusCode__());
    }

    
}
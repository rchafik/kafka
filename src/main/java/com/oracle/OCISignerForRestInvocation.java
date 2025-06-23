package com.oracle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.tomitribe.auth.signatures.MissingRequiredHeaderException;
import org.tomitribe.auth.signatures.PEM;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/*
    Reference: https://blogs.oracle.com/ee-ces/post/oci-rest-api-made-simple---get-request-in-java
    Scenario: You want to invoke a REST API using the OCI Java SDK, but you want to avoid using the host header for signing the request.
        User -> API Gateway -> Streaming
    Lembrando que para autenticar no Streaming, o FQND é obrigatório, por isso, essa solução paliativa no cabeçalho de segurança.
*/


public class OCISignerForRestInvocation
{
    // The following paramters are mandatory
    // Tenancy OCID
    private static String m_tenancyOcId = "ocid1.tenancy.oc1..aaaaaaaaoi6b5sxlv4z773boczybqz3h2vspvvru42jysvizl77lky22ijaq";
    // User OCID
    private static String m_userOcid = "ocid1.user.oc1..aaaaaaaay2d4liddktpoai5xaoli63bnnzfelywwmsje2xvdgcusnxdkad3q";
    // FingerPrint
    private static String m_fingerPrint = "6a:96:c5:9a:c6:4d:7a:bd:b7:05:67:7a:dc:f8:bc:6e";
    // Complete path to the private key file
    private static String m_privateKeyFilename = "/home/opc/.oci/oci_api_key.pem";
    // Private key path in Unix
    //private static String p_privateKeyFilename = "/home/oracle/oci_api_key.pem";


    public static void main(String[] args) throws UnsupportedEncodingException
    {
        String uri = "https://hjabyoi4f6pinyo3ukvlwmcnri.apigateway.sa-saopaulo-1.oci.customer-oci.com/20180418/streams/ocid1.stream.oc1.sa-saopaulo-1.amaaaaaafioir7iacg2i44q2qotiww2mpglsabswlu53wu2uz3rzpxzdpavq/groupCursors";

        OCISignerForRestInvocation ociObj = new OCISignerForRestInvocation();
        //ociObj.getSignHeaders(m_tenancyOcId,m_userOcid,m_fingerPrint,m_privateKeyFilename,uri);

        ociObj.getAPIResponse(m_tenancyOcId,m_userOcid,m_fingerPrint,m_privateKeyFilename,uri);
    }


    public String getAPIResponse(String tenancyOcid,
                                 String userOcid,
                                 String fingerPrint,
                                 String privateKeyFile,
                                 String uri) throws UnsupportedEncodingException {
        HttpPost request;
        String responseAsString = null;

        String apiKey = (tenancyOcid+"/"
                                + userOcid+"/"
                                + fingerPrint);

        PrivateKey privateKey = loadPrivateKey(privateKeyFile);
        RequestSigner signer = new RequestSigner(apiKey, privateKey);

        request = new HttpPost(uri);
        String requestBody = "{\"groupName\": \"MyTestGroupName\",\"type\": \"TRIM_HORIZON\"}";
        StringEntity entity = new StringEntity(requestBody);
        request.setEntity(entity);
        signer.signRequest(request);

        CloseableHttpClient client = HttpClientBuilder.create().build();

        // In case you need a proxy, please uncomment the followin code

        /*HttpHost proxy = new HttpHost("proxy.example.com", 80);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        request.setConfig(config);*/

        try
        {

            HttpResponse response = client.execute(request);
            responseAsString = EntityUtils.toString(response.getEntity());
            System.out.println(responseAsString);

        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return responseAsString;
    }



    public HashMap<String,String> getSignHeaders(String tenancyOcid,
                                                 String userOcid,
                                                 String fingerPrint,
                                                 String privateKeyFile,
                                                 String uri)
    {
        HashMap<String,String> headers = new HashMap<String,String>();
        HttpRequestBase request;

        // This is the keyId for a key uploaded through the console

        String apiKey = (tenancyOcid+"/"
                                + userOcid+"/"
                                + fingerPrint);
        // Private key file path in unix
        //String privateKeyFilename = "/home/oracle/oci_api_key.pem";
        PrivateKey privateKey = loadPrivateKey(privateKeyFile);
        RequestSigner signer = new RequestSigner(apiKey, privateKey);

        System.out.println(uri);
        request = new HttpGet(uri);

        signer.signRequest(request);
        Header[] auth = request.getHeaders("Authorization");
        Header[] date = request.getHeaders("date");
        Header[] host = request.getHeaders("host");
        System.out.println("Authorization Header: " + auth[0].toString().substring(15, auth[0].toString().length()));
        System.out.println("date Header: " + date[0].toString().substring(6, date[0].toString().length()));
        System.out.println("host Header: " + host[0].toString().substring(6, host[0].toString().length()));
        headers.put("Authorization",auth[0].toString().substring(15, auth[0].toString().length()));
        headers.put("date",date[0].toString().substring(6, date[0].toString().length()));
        headers.put("host",host[0].toString().substring(6, host[0].toString().length()));

        return headers;

    }


    private static PrivateKey loadPrivateKey(String privateKeyFilename) {
        try (InputStream privateKeyStream = Files.newInputStream(Paths.get(privateKeyFilename))){
            return PEM.readPrivateKey(privateKeyStream);
        } catch (InvalidKeySpecException e) {
                throw new RuntimeException("Invalid format for private key");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load private key");
        }
    }


    public static class RequestSigner {
        private static final SimpleDateFormat DATE_FORMAT;
        private static final String SIGNATURE_ALGORITHM = "rsa-sha256";
        private static final Map<String, List<String>> REQUIRED_HEADERS;
        static {
            DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
            REQUIRED_HEADERS = ImmutableMap.<String, List<String>>builder()
                    .put("get", ImmutableList.of("date", "(request-target)", "host"))
                    .put("head", ImmutableList.of("date", "(request-target)", "host"))
                    .put("delete", ImmutableList.of("date", "(request-target)", "host"))
                    .put("put", ImmutableList.of("date", "(request-target)", "host", "content-length", "content-type", "x-content-sha256"))
                    .put("post", ImmutableList.of("date", "(request-target)", "content-type", "x-content-sha256"))
            .build();
        }
        private final Map<String, Signer> signers;

        public RequestSigner(String apiKey, Key privateKey) {
            this.signers = REQUIRED_HEADERS
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> buildSigner(apiKey, privateKey, entry.getKey())));
        }

        protected Signer buildSigner(String apiKey, Key privateKey, String method) {
            final Signature signature = new Signature(
                    apiKey, SIGNATURE_ALGORITHM, null, REQUIRED_HEADERS.get(method.toLowerCase()));
            return new Signer(privateKey, signature);
        }

        public void signRequest(HttpRequestBase request) {
            final String method = request.getMethod().toLowerCase();
            // nothing to sign for options
            if (method.equals("options")) {
                return;
            }

            final String path = extractPath(request.getURI());

            // supply date if missing
            if (!request.containsHeader("date")) {
                request.addHeader("date", DATE_FORMAT.format(new Date()));
            }

            // supply host if mossing
            if (!request.containsHeader("host")) {
                request.addHeader("host", request.getURI().getHost());
            }

            // supply content-type, content-length, and x-content-sha256 if missing (PUT and POST only)
            if (method.equals("put") || method.equals("post")) {
                if (!request.containsHeader("content-type")) {
                    request.addHeader("content-type", "application/json");
                }
                if (!request.containsHeader("content-length") || !request.containsHeader("x-content-sha256")) {
                    byte[] body = getRequestBody((HttpEntityEnclosingRequestBase) request);
                    if (!request.containsHeader("content-length")) {
//                        request.addHeader("content-length", Integer.toString(body.length));
                    }
                    if (!request.containsHeader("x-content-sha256")) {
                        request.addHeader("x-content-sha256", calculateSHA256(body));
                    }
                }
            }

            final Map<String, String> headers = extractHeadersToSign(request);
            final String signature = this.calculateSignature(method, path, headers);
            request.setHeader("Authorization", signature);
        }


        private static String extractPath(URI uri) {
            String path = uri.getRawPath();
            String query = uri.getRawQuery();
            if (query != null && !query.trim().isEmpty()) {
                path = path + "?" + query;
            }
            return path;
        }

        private static Map<String, String> extractHeadersToSign(HttpRequestBase request) {
            List<String> headersToSign = REQUIRED_HEADERS.get(request.getMethod().toLowerCase());
            if (headersToSign == null) {
                throw new RuntimeException("Don't know how to sign method " + request.getMethod());
            }
            return headersToSign.stream()
                    // (request-target) is a pseudo-header
                    .filter(header -> !header.toLowerCase().equals("(request-target)"))
                    .collect(Collectors.toMap(
                    header -> header,
                    header -> {
                        if (!request.containsHeader(header)) {
                            throw new MissingRequiredHeaderException(header);
                        }
                        if (request.getHeaders(header).length > 1) {
                            throw new RuntimeException(
                                    String.format("Expected one value for header %s", header));
                        }
                        return request.getFirstHeader(header).getValue();
                    }));
        }

        private String calculateSignature(String method, String path, Map<String, String> headers) {
            Signer signer = this.signers.get(method);
            if (signer == null) {
                throw new RuntimeException("Don't know how to sign method " + method);
            }
            try {
                return signer.sign(method, path, headers).toString();
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate signature", e);
            }
        }

        private String calculateSHA256(byte[] body) {
            byte[] hash = Hashing.sha256().hashBytes(body).asBytes();
            return Base64.getEncoder().encodeToString(hash);
        }

        private byte[] getRequestBody(HttpEntityEnclosingRequestBase request) {
            HttpEntity entity = request.getEntity();
            // null body is equivalent to an empty string
            if (entity == null) {
                return "".getBytes(StandardCharsets.UTF_8);
            }
            // May need to replace the request entity after consuming
            boolean consumed = !entity.isRepeatable();
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            try {
                entity.writeTo(content);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy request body", e);
            }
            // Replace the now-consumed body with a copy of the content stream
            byte[] body = content.toByteArray();
            if (consumed) {
                request.setEntity(new ByteArrayEntity(body));
            }
            return body;
        }
    }
}

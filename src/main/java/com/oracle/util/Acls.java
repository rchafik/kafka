package com.oracle.util;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeAclsResult;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Acls {
    public static void main(String[] args) {
        System.out.println("conectando ao kafka...");
        AdminClient adminClient = createAdminClient();
        System.out.println("conectado!");

        //listAcls(adminClient);
        listAcls(adminClient, "yoda-mtls-topic");
        //deleteAcls(adminClient, "yoda-mtls-topic", "User:CN=yoda");
        createAcls(adminClient, "yoda-mtls-topic", "User:CN=yoda");
        listAcls(adminClient, "yoda-mtls-topic");

        closeAdminClient(adminClient);
        System.out.println("conexão encerrada!");
    }

    public static AdminClient createAdminClient() {
        Properties props = new Properties();
        // Desta vez usamos variável de ambiente para guardar as informações sensíveis, finalmente, né?
        // export BOOTSTRAP_SERVERS=bootstrap-clstr-xxx.kafka.sa-saopaulo-1.oci.oraclecloud.com:9092
        props.put("bootstrap.servers", System.getenv("BOOTSTRAP_SERVERS"));
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-512");        
        // export SASL_JAAS_CONFIG='org.apache.kafka.common.security.scram.ScramLoginModule required username="super-user" password="xxx";'
        // para realizar atividades de ACLS use o super-user
        props.put("sasl.jaas.config", System.getenv("SASL_JAAS_CONFIG"));
        AdminClient adminClient = AdminClient.create(props);        
        return adminClient;
    }

    public static void closeAdminClient(AdminClient adminClient) {
        if (adminClient != null) {
            adminClient.close();
        }
    }

    public static void listAcls(AdminClient adminClient) {
        DescribeAclsResult describeAclsResult = adminClient.describeAcls(AclBindingFilter.ANY);
        try {
            describeAclsResult.values().get().forEach(acl -> System.out.println("ACL: " + acl));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void listAcls(AdminClient adminClient, String topic) {
        try {
            AclBindingFilter topicFilter = new AclBindingFilter(
                new ResourcePatternFilter(ResourceType.TOPIC, topic, PatternType.LITERAL), 
                AccessControlEntryFilter.ANY
            );
            adminClient.describeAcls(topicFilter).values().get().forEach(acl -> System.out.println("Filtered ACL: " + acl));            
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }    

    public static void createAcls(AdminClient adminClient, String topic, String principal) {
        String host = "*";
        AclBinding aclBinding = new AclBinding(
            new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL),
            new AccessControlEntry(principal, host, AclOperation.READ, AclPermissionType.ALLOW)
        );

        try {
            adminClient.createAcls(java.util.Collections.singleton(aclBinding)).all().get();
            System.out.println("ACL created successfully.");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

    public static void deleteAcls(AdminClient adminClient, String topic, String principal) {
        String host = "*";
        AclBindingFilter deleteFilter = new AclBindingFilter(
            new ResourcePatternFilter(ResourceType.TOPIC, topic, PatternType.LITERAL),
            new AccessControlEntryFilter(principal, host, AclOperation.READ, AclPermissionType.ALLOW)
        );

        try {
            adminClient.deleteAcls(java.util.Collections.singleton(deleteFilter)).all().get();
            System.out.println("ACL deleted successfully.");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }    

}

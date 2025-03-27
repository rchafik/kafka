package com.oracle.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class OracleTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("oracle.jdbc.DiagnosabilityEnabled", "true");
        Class.forName("oracle.jdbc.OracleDriver");
        String url = "jdbc:oracle:thin:@chafik_high?TNS_ADMIN=/home/opc/labKafkaConnector/database/wallet/";
        Connection conn = DriverManager.getConnection(url, "kafkademo", "ateamKafka#123");
        System.out.println("Conexão bem-sucedida!");
        ResultSet rs = conn.prepareStatement("select count(*) qtd from teste").executeQuery();
        if (rs.next()) {
            System.out.println("Total de registros: " + rs.getInt("qtd"));
        }
        rs.close();
        conn.close();
    }
}
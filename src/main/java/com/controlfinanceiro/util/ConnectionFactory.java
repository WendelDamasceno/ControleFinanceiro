package com.controlfinanceiro.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory {

    private static final String PROPERTIES_FILE = "/database.properties";
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConnectionFactory.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Arquivo de propriedades do banco não encontrado: " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar propriedades do banco de dados", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            String url = getProperty("db.url");
            String username = getProperty("db.username");
            String password = getProperty("db.password");
            String driver = getProperty("db.driver");

            if (url == null || username == null || driver == null) {
                throw new SQLException("Propriedades do banco de dados não configuradas corretamente");
            }

            // Carrega o driver JDBC
            Class.forName(driver);

            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado", e);
        }
    }

    private static String getProperty(String key) {
        // Primeiro tenta pegar das variáveis de ambiente
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        // Se não encontrar na variável de ambiente, pega do arquivo properties
        return properties.getProperty(key);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
}

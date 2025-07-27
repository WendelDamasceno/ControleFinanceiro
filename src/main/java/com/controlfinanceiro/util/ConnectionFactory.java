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
            String url = properties.getProperty("database.url");
            String username = properties.getProperty("database.username");
            String password = properties.getProperty("database.password");
            String driver = properties.getProperty("database.driver");

            if (url == null || username == null || password == null || driver == null) {
                throw new SQLException("Propriedades do banco de dados não configuradas corretamente");
            }

            // Carrega o driver JDBC
            Class.forName(driver);

            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado", e);
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

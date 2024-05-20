package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Veritabani {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=yemliha;encrypt=true;trustServerCertificate=true";
    private static final String USER = "cumaliyelbiz";
    private static final String PASS = "14531453";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("MSSQL JDBC sürücüsü bulunamadı: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Veritabanı bağlantısı başarılı!");
        } catch (SQLException e) {
            System.out.println("Veritabanı bağlantısı başarısız: " + e.getMessage());
        }
        return connection;
    }
}


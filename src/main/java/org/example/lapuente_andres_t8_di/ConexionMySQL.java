package org.example.lapuente_andres_t8_di;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/gestion_restaurante";
    private final String user = "root";
    private final String password = "root";

    public ConexionMySQL() {
        establecerConexion(); // Llamamos al método para inicializar la conexión
    }

    public Connection getConnection() {
        return connection;
    }

    public void establecerConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión a la base de datos establecida.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cerrarConexion() {
        if (connection != null) {  // Verificación antes de cerrar
            try {
                connection.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }
    }
}

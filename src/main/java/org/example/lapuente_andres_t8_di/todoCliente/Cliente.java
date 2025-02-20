package org.example.lapuente_andres_t8_di.todoCliente;


import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cliente {

    private int id;

    private String nombre;

    private String telefono;

    private String direccion;

    // Constructor con parámetros
    public Cliente(int id, String nombre, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Cliente(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return nombre;  // Solo muestra el nombre
    }

    public static void llenarInformacionCliente(Connection connection, ObservableList<Cliente> lista) throws SQLException {
        Statement instruccion = connection.createStatement();
        ResultSet resultado = instruccion.executeQuery("SELECT * FROM clientes");
        while (resultado.next()) {
            lista.add(new Cliente(resultado.getInt("id"), resultado.getString("nombre"), resultado.getString("telefono"), resultado.getString("direccion")));
        }
    }

    public static List<Cliente> obtenerClientes(Connection connection) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT id, nombre FROM clientes"; // Ajusta el nombre de la tabla

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                // Verificar que no sean null antes de agregar a la lista
                if (id != 0 && nombre != null && !nombre.trim().isEmpty()) {
                    System.out.println("Cliente encontrado: id=" + id + ", nombre=" + nombre);
                    clientes.add(new Cliente(id, nombre));
                } else {
                    System.out.println("Cliente con id=" + id + " tiene datos incompletos (nombre: " + nombre + ")");
                }
            }
        }
        return clientes;
    }

}
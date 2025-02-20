package org.example.lapuente_andres_t8_di.todoProducto;

import javafx.collections.ObservableList;
import org.example.lapuente_andres_t8_di.todoCliente.Cliente;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Producto {
    private int id;
    private String nombre;
    private String categoria;
    private double precio;
    private boolean disponibilidad;

    // Constructor vacío
    public Producto() {}

    // Constructor con parámetros
    public Producto(int id, String nombre, String categoria, double precio, boolean disponibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.disponibilidad = disponibilidad;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public String getPrecioString() {
        return String.valueOf(precio);
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    // Método para modificar el precio
    public void modificarPrecio(double nuevoPrecio) {
        this.precio = nuevoPrecio;
    }

    // Método para cambiar la disponibilidad
    public void cambiarDisponibilidad(boolean nuevaDisponibilidad) {
        this.disponibilidad = nuevaDisponibilidad;
    }

    // Método toString para representar el objeto como cadena
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", disponibilidad=" + (disponibilidad ? "Disponible" : "No disponible") +
                '}';
    }

    public static void llenarInformacionProducto(Connection connection, ObservableList<Producto> lista) throws SQLException {
        Statement instruccion = connection.createStatement();
        ResultSet resultado = instruccion.executeQuery("SELECT * FROM productos");
        while (resultado.next()) {
            lista.add(new Producto(resultado.getInt("id"), resultado.getString("nombre"),
                    resultado.getString("categoria"), resultado.getDouble("precio"),resultado.getBoolean("disponibilidad")));
        }
    }
}
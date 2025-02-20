package org.example.lapuente_andres_t8_di.todoDetPedido;

import javafx.collections.ObservableList;
import org.example.lapuente_andres_t8_di.todoPedido.Pedido;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DetallePedido {
    private int idDetallePedido;
    private int idPedido;
    private int idProducto;
    private int cantidad;
    private double precio;
    private double subtotal;

    // Constructor
    public DetallePedido(int idDetallePedido, int idPedido, int idProducto, int cantidad, double precio, double subtotal) {
        this.idDetallePedido = idDetallePedido;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public int getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(int idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public static void llenarInformacionDetPedido(Connection connection, ObservableList<DetallePedido> lista) throws SQLException {
        Statement instruccion = connection.createStatement();
        ResultSet resultado = instruccion.executeQuery("SELECT * FROM detalle_pedidos");
        while (resultado.next()) {
            lista.add(new DetallePedido(resultado.getInt("id_detalle_pedido"), resultado.getInt("id_pedido"), resultado.getInt("id_producto"),
                    resultado.getInt("cantidad"),resultado.getDouble("precio"),resultado.getDouble("subtotal")));
        }
    }
}
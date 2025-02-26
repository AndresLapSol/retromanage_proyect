package org.example.lapuente_andres_t8_di.todoDetPedido;

import javafx.collections.ObservableList;
import javafx.scene.control.SpinnerValueFactory;

import java.sql.*;

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

    public SpinnerValueFactory setCantidad(int cantidad) {
        this.cantidad = this.cantidad;
        return null;
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
            lista.add(new DetallePedido(resultado.getInt("id_detalle_pedido"),
                    resultado.getInt("id_pedido"),
                    resultado.getInt("id_producto"),
                    resultado.getInt("cantidad"),
                    resultado.getDouble("precio"),
                    resultado.getDouble("subtotal")));
        }
    }

    public static void llenarInformacionDetPedidoPorPedido(Connection conn, ObservableList<DetallePedido> lista, int idPedido) {
        String sql = "SELECT * FROM detalle_pedidos WHERE id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = new DetallePedido(
                            rs.getInt("id_detalle_pedido"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_producto"),
                            rs.getInt("cantidad"),
                            rs.getDouble("precio"),
                            rs.getDouble("subtotal")
                    );
                    lista.add(detalle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
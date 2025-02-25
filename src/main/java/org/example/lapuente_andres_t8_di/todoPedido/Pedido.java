package org.example.lapuente_andres_t8_di.todoPedido;

import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido {
    private int idPedido;
    private int idCliente;
    private Date fechaPedido;
    private Time horaPedido;
    private double total;
    private String estadoPedido;

    // Constructor
    public Pedido(int idPedido, int idCliente, Date fechaPedido, Time horaPedido, double total, String estadoPedido) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.horaPedido = horaPedido;
        this.total = total;
        this.estadoPedido = estadoPedido;
    }

    public Pedido(int idPedido) {
        this.idPedido = idPedido;
    }

    // Getters y Setters
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }



    public int getIdCliente() {
        return idCliente;
    }

    public void setCliente(int cliente) {
        this.idCliente = cliente;
    }

    public LocalDate getFechaPedido() {
        return ((java.sql.Date) fechaPedido).toLocalDate();
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Time getHoraPedido() {
        return horaPedido;
    }

    public void setHoraPedido(Date horaPedido) {
        this.horaPedido = (Time) horaPedido;
    }

    public double getTotal() {
        return total;
    }

    public String getTotalString() {
        return String.valueOf(total);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstadoPedido() {
        return estadoPedido != null ? estadoPedido : "En Preparación"; // Valor por defecto
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public static void llenarInformacionPedido(Connection connection, ObservableList<Pedido> lista) throws SQLException {
        Statement instruccion = connection.createStatement();
        ResultSet resultado = instruccion.executeQuery("SELECT * FROM pedidos");
        while (resultado.next()) {
            lista.add(new Pedido(resultado.getInt("id_pedido"), resultado.getInt("id_cliente"), resultado.getDate("fecha_pedido"),
                    resultado.getTime("hora_pedido"),resultado.getDouble("total"),resultado.getString("estado_pedido")));
        }
    }

    public static List<Pedido> obtenerPedidos(Connection connection) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String query = "SELECT id_pedido FROM pedidos"; // Ajusta el nombre de la tabla

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id_pedido = rs.getInt("id_pedido");

                // Verificar que no sean null antes de agregar a la lista
                if (id_pedido != 0) {
                    System.out.println("Cliente encontrado: id=" + id_pedido );
                    pedidos.add(new Pedido(id_pedido));
                } else {
                    System.out.println("Cliente con id=" + id_pedido );
                }
            }
        }
        return pedidos;
    }

    public int getId() {
        return idPedido;
    }
}
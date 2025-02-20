package org.example.lapuente_andres_t8_di.todoPedido;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;

public class Pedido {
    private int idPedido;
    private int idCliente;
    private Date fechaPedido;
    private Date horaPedido;
    private double total;
    private String estadoPedido;

    // Constructor
    public Pedido(int idPedido, int idCliente, Date fechaPedido, Date horaPedido, double total, String estadoPedido) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.horaPedido = horaPedido;
        this.total = total;
        this.estadoPedido = estadoPedido;
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

    public Date getHoraPedido() {
        return horaPedido;
    }

    public void setHoraPedido(Date horaPedido) {
        this.horaPedido = horaPedido;
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
        return estadoPedido;
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

    public int getId() {
        return idPedido;
    }
}
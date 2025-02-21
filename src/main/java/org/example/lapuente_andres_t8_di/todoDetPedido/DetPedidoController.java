package org.example.lapuente_andres_t8_di.todoDetPedido;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.example.lapuente_andres_t8_di.ConexionMySQL;
import org.example.lapuente_andres_t8_di.todoCliente.Cliente;
import org.example.lapuente_andres_t8_di.todoPedido.Pedido;
import org.example.lapuente_andres_t8_di.todoProducto.Producto;

import java.sql.*;
import java.util.List;

public class DetPedidoController {

    @FXML private Spinner spinnerCantidad;
    @FXML private ChoiceBox choiceBoxProducto;
    @FXML private TableColumn<DetallePedido, Double> clmnSubtotal;
    @FXML private TableColumn<DetallePedido, Double> clmnPrecio;
    @FXML private TableColumn<DetallePedido, Integer> clmnCantidad;
    @FXML private TableColumn<DetallePedido, Integer> clmnIdProducto;
    @FXML private TableColumn<DetallePedido, Integer> clmnIdPedido;
    @FXML private TableColumn<DetallePedido, Integer> clmnIdDetPedido;
    @FXML private TableView<DetallePedido> tablaDetPedido;
    @FXML private ObservableList<DetallePedido> listaDetPedidos;

    private ConexionMySQL conexion;
    private Connection conn; // Conexión a nivel de clase

    public DetPedidoController() {
        // Inicializar la conexión en el constructor
        conexion = new ConexionMySQL();
    }

    public void initialize() throws SQLException {
        // Establecer la conexión una vez
        conn = conexion.getConnection();

        if (conn == null) {
            System.err.println("Error: la conexión a la base de datos es NULL.");
            return;

        }
        listaDetPedidos = FXCollections.observableArrayList();
        DetallePedido.llenarInformacionDetPedido(conn, listaDetPedidos);  // Pasamos la conexión ya establecida

        tablaDetPedido.setItems(listaDetPedidos);

        // Enlazar columnas con atributos
        //clmnIdPedido.setCellValueFactory(new PropertyValueFactory<Pedido, Integer>("idPedido"));
        clmnIdDetPedido.setCellValueFactory(new PropertyValueFactory<DetallePedido, Integer>("idDetallePedido"));
        clmnIdPedido.setCellValueFactory(new PropertyValueFactory<DetallePedido, Integer>("idPedido"));
        clmnIdProducto.setCellValueFactory(new PropertyValueFactory<DetallePedido, Integer>("idProducto"));
        clmnCantidad.setCellValueFactory(new PropertyValueFactory<DetallePedido, Integer>("cantidad"));
        clmnPrecio.setCellValueFactory(new PropertyValueFactory<DetallePedido, Double>("precio"));
        clmnSubtotal.setCellValueFactory(new PropertyValueFactory<DetallePedido, Double>("subtotal"));


        // Añadir opciones a las ChoiceBox
        // Obtener los productos desde la base de datos
        List<Producto> productos = Producto.obtenerProductos(conn);
        System.out.println("Productos en lista: " + productos.size());

        // Si la lista tiene datos, la agregamos al ChoiceBox
        if (productos != null && !productos.isEmpty()) {
            choiceBoxProducto.setItems(FXCollections.observableArrayList(productos));

            // Usamos un StringConverter para mostrar solo el nombre del producto
            choiceBoxProducto.setConverter(new StringConverter<Producto>() {
                @Override
                public String toString(Producto producto) {
                    return (producto != null) ? producto.getNombre() : "";
                }

                @Override
                public Producto fromString(String string) {
                    // No es necesario implementar esta función en este caso
                    return null;
                }
            });
        } else {
            System.out.println("No se encontraron productos en la base de datos.");
        }

        // Establecer el valor mínimo y máximo del Spinner
        spinnerCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));


    }

    private void actualizarTotalPedido(int idPedido) {
        String sql = "UPDATE pedidos SET total = (SELECT COALESCE(SUM(subtotal), 0) FROM detalle_pedidos WHERE id_pedido = ?) WHERE id_pedido = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idPedido);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void insertarDetalles() throws SQLException {

        String sql = "INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio, subtotal) VALUES (?, ?, ?, ?, ?)";
        List<Pedido> pedidos = Pedido.obtenerPedidos(conn);

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos disponibles.");
            return;
        }

        // Obtener el último pedido registrado
        Pedido ultimoPedido = pedidos.get(pedidos.size() - 1);

        // Obtener el producto seleccionado
        Producto productoSeleccionado = (Producto) choiceBoxProducto.getValue();

        if (productoSeleccionado == null) {
            System.out.println("Debe seleccionar un producto.");
            return;
        }

        int cantidad = (Integer) spinnerCantidad.getValue();
        double precioProducto = productoSeleccionado.getPrecio();
        double subtotalSeleccionado = cantidad * productoSeleccionado.getPrecio();
        System.out.println("Producto: " + productoSeleccionado.getNombre());
        System.out.println("Precio del producto: " + precioProducto);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Subtotal calculado: " + subtotalSeleccionado);




        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ultimoPedido.getIdPedido());
            stmt.setInt(2, productoSeleccionado.getId());
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, productoSeleccionado.getPrecio());
            stmt.setDouble(5, subtotalSeleccionado);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Detalle de pedido insertado correctamente.");

                // Actualizar el total en la tabla `pedidos`
                actualizarTotalPedido(ultimoPedido.getIdPedido());

                // Refrescar la tabla
                listaDetPedidos.add(new DetallePedido(
                        0,
                        ultimoPedido.getIdPedido(),
                        productoSeleccionado.getId(),
                        cantidad,
                        productoSeleccionado.getPrecio(),
                        subtotalSeleccionado
                ));
                tablaDetPedido.getItems().clear();
                DetallePedido.llenarInformacionDetPedido(conn, listaDetPedidos);
                tablaDetPedido.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

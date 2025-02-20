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

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
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
        clmnIdDetPedido.setCellValueFactory(new PropertyValueFactory<DetallePedido, Integer>("idDetPedido"));
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
}

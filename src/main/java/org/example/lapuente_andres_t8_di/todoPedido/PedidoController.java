package org.example.lapuente_andres_t8_di.todoPedido;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.lapuente_andres_t8_di.ConexionMySQL;
import org.example.lapuente_andres_t8_di.todoCliente.Cliente;
import org.example.lapuente_andres_t8_di.todoProducto.Producto;


import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class PedidoController {

    @FXML private ChoiceBox<String> choiceBoxEstado;
    @FXML private Label volverMenu;
    @FXML private TextField textFieldTotal;
    @FXML private TextField textFieldHora;
    @FXML private DatePicker datePickerFecha;
    @FXML private ChoiceBox<Cliente> choiceBoxCliente;
    @FXML private TableColumn<Pedido, Integer> clmnIdPedido;
    @FXML private TableColumn<Pedido, Integer> clmnIdCliente;
    @FXML private TableColumn<Pedido, Date> clmnFecha;
    @FXML private TableColumn<Pedido, Time> clmnHora;
    @FXML private TableColumn<Pedido, Double> clmnTotal;
    @FXML private TableColumn<Pedido, String> clmnEstado;
    @FXML private TableView<Pedido> tablaPedido;
    @FXML private ObservableList<Pedido> listaPedidos;


    private ConexionMySQL conexion;
    private Connection conn; // Conexión a nivel de clase

    public PedidoController() {
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

        listaPedidos = FXCollections.observableArrayList();
        Pedido.llenarInformacionPedido(conn, listaPedidos);  // Pasamos la conexión ya establecida

        tablaPedido.setItems(listaPedidos);

        // Enlazar columnas con atributos
        clmnIdPedido.setCellValueFactory(new PropertyValueFactory<Pedido, Integer>("idPedido"));
        clmnIdCliente.setCellValueFactory(new PropertyValueFactory<Pedido, Integer>("idCliente"));
        clmnFecha.setCellValueFactory(new PropertyValueFactory<Pedido, Date>("fechaPedido"));
        clmnHora.setCellValueFactory(new PropertyValueFactory<Pedido, Time>("horaPedido"));
        clmnTotal.setCellValueFactory(new PropertyValueFactory<Pedido, Double>("total"));
        clmnEstado.setCellValueFactory(new PropertyValueFactory<Pedido, String>("estadoPedido"));


        // Añadir opciones a las ChoiceBox
        List<Cliente> clientes = Cliente.obtenerClientes(conn);
        System.out.println("Clientes en lista: " + clientes.size());  // Verifica cuántos clientes hay en la lista

        // Si la lista tiene datos, la agregamos al ChoiceBox
        if (clientes != null && !clientes.isEmpty()) {
            choiceBoxCliente.setItems(FXCollections.observableArrayList(clientes));
        } else {
            System.out.println("No se encontraron clientes en la base de datos.");
        }

        choiceBoxEstado.getItems().addAll(
                "En Preparación",
                "En Camino",
                "Entregado"

        );

        //Inicializar configurador de los textfields de Hora y total
        configurarTextFieldHora();


        // Seleccionar un cliente al hacer doble clic
        tablaPedido.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pedido pedidoSeleccionado = tablaPedido.getSelectionModel().getSelectedItem();
                if (pedidoSeleccionado != null) {
                    int idClientePedido = pedidoSeleccionado.getIdCliente();
                    // Buscar el Cliente con ese id en el ChoiceBox
                    for (Cliente cliente : choiceBoxCliente.getItems()) {
                        if (cliente.getId() == idClientePedido) {
                            choiceBoxCliente.setValue(cliente);
                            break;
                        }
                    }
                    datePickerFecha.setValue(pedidoSeleccionado.getFechaPedido());
                    // Formatear la hora a "HH:mm" antes de asignarla
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    textFieldHora.setText(sdf.format(pedidoSeleccionado.getHoraPedido()));

                    textFieldTotal.setText(pedidoSeleccionado.getTotalString());
                    choiceBoxEstado.setValue(pedidoSeleccionado.getEstadoPedido());
                }
            }
        });


    }

    //CONFIGURAR TEXTFIELDS
    private void configurarTextFieldHora() {
        // Expresión regular para el formato de hora (HH:mm)
        Pattern pattern = Pattern.compile("([01]?[0-9]|2[0-3]):?[0-5]?[0-9]?");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Permitir la cadena vacía para poder borrar el contenido
            if (newText.isEmpty() || pattern.matcher(newText).matches()) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textFieldHora.setTextFormatter(textFormatter);
    };

    @FXML
    protected void abrirDetPedido() {
        try {
            // Cargar el archivo FXML de la nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/lapuente_andres_t8_di/detpedido-view.fxml"));
            Scene nuevaEscena = new Scene(fxmlLoader.load());

            // Crear un nuevo Stage para la ventana secundaria
            Stage nuevaVentana = new Stage();
            nuevaVentana.setScene(nuevaEscena);

            // Mostrar la nueva ventana
            nuevaVentana.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

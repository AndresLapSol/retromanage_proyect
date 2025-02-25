package org.example.lapuente_andres_t8_di.todoPedido;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.lapuente_andres_t8_di.ConexionMySQL;
import org.example.lapuente_andres_t8_di.todoCliente.Cliente;
import org.example.lapuente_andres_t8_di.todoDetPedido.DetPedidoController;
import org.example.lapuente_andres_t8_di.todoDetPedido.DetallePedido;
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
    private static PedidoController instancia;


    private ConexionMySQL conexion;
    private Connection conn; // Conexión a nivel de clase

    public PedidoController() {
        instancia = this;
        // Inicializar la conexión en el constructor
        conexion = new ConexionMySQL();
    }
    public static PedidoController getInstancia() {
        return instancia;
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

                    // Formatear la hora a "HH:mm"
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    textFieldHora.setText(sdf.format(pedidoSeleccionado.getHoraPedido()));

                    // Verifica que estadoPedido no sea null antes de asignarlo
                    if (pedidoSeleccionado.getEstadoPedido() != null) {
                        choiceBoxEstado.setValue(pedidoSeleccionado.getEstadoPedido());
                    } else {
                        choiceBoxEstado.setValue("En Preparación"); // Valor por defecto si es null
                    }
                }
            }
        });


    }

    private Cliente obtenerClientePorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("direccion")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

        // Crear un nuevo pedido
        String sql = "INSERT INTO pedidos (id_cliente, fecha_pedido, hora_pedido, estado_pedido) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            Cliente clienteSeleccionado = choiceBoxCliente.getValue();
            if (clienteSeleccionado == null) {
                System.out.println("Error: No se ha seleccionado un cliente.");
                return;
            }

            LocalDate fechaPedido = datePickerFecha.getValue();
            if (fechaPedido == null) {
                System.out.println("Error: No se ha seleccionado una fecha.");
                return;
            }

            String horaTexto = textFieldHora.getText();
            if (horaTexto.matches("\\d{1,2}:\\d{2}")) {
                horaTexto += ":00";  // Agregar segundos si falta
            }

            Time hora;
            try {
                hora = Time.valueOf(horaTexto);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Formato de hora incorrecto.");
                return;
            }

            // Verificar si se seleccionó un estado, si no, usar "En Preparación"
            String estadoPedido = (choiceBoxEstado.getValue() != null)
                    ? choiceBoxEstado.getValue()
                    : "En Preparación";

            stmt.setInt(1, clienteSeleccionado.getId());
            stmt.setDate(2, Date.valueOf(fechaPedido));
            stmt.setTime(3, hora);
            stmt.setString(4, estadoPedido);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Pedido insertado correctamente.");

                listaPedidos.add(new Pedido(
                        0,
                        clienteSeleccionado.getId(),
                        Date.valueOf(fechaPedido),
                        Time.valueOf(horaTexto),
                        0,
                        estadoPedido
                ));
                tablaPedido.getItems().clear();
                Pedido.llenarInformacionPedido(conn, listaPedidos);
                tablaPedido.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarTotalPedidoUI(int idPedido) {
        String sql = "SELECT total FROM pedidos WHERE id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalActualizado = rs.getDouble("total");
                    // Buscar el pedido en la lista y actualizar su total
                    for (Pedido pedido : listaPedidos) {
                        if (pedido.getIdPedido() == idPedido) {
                            pedido.setTotal(totalActualizado);
                            break;
                        }
                    }
                    tablaPedido.refresh();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void modificarDetPedido() {
        // Obtener el pedido seleccionado en la tabla de pedidos
        Pedido pedidoSeleccionado = tablaPedido.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modificar Detalles");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un pedido de la tabla.");
            alert.showAndWait();
            return;
        }

        // Crear una lista observable para los detalles del pedido seleccionado
        ObservableList<DetallePedido> listaDetalles = FXCollections.observableArrayList();
        String sql = "SELECT * FROM detalle_pedidos WHERE id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoSeleccionado.getIdPedido());
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
                    listaDetalles.add(detalle);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudieron obtener los detalles del pedido.");
            alert.showAndWait();
            return;
        }

        // Cargar la ventana de modificación usando el FXML que ya tienes
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/lapuente_andres_t8_di/detpedido-view.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la ventana de modificación
            DetPedidoController detController = loader.getController();
            detController.setListaDetalles(listaDetalles);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modificar Detalles del Pedido");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir la ventana para modificar los detalles.");
            alert.showAndWait();
        }
    }





}

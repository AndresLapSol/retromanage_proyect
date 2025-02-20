package org.example.lapuente_andres_t8_di.todoPedido;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.lapuente_andres_t8_di.ConexionMySQL;
import org.example.lapuente_andres_t8_di.todoCliente.Cliente;
import org.example.lapuente_andres_t8_di.todoProducto.Producto;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class PedidoController {

    @FXML private Label volverMenu;
    @FXML private ChoiceBox choiceBoxEstado;
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
        configurarTextFieldTotal();



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
    }

    private void configurarTextFieldTotal() {
        // Expresión regular para permitir solo números decimales con signo opcional
        Pattern pattern = Pattern.compile("-?\\d*(\\.\\d*)?");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (pattern.matcher(newText).matches()) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textFieldTotal.setTextFormatter(textFormatter);
    }

    // Crear un nuevo pedidos
    @FXML
    public void insertarPedido() {
        String sql = "INSERT INTO pedidos (id_cliente, fecha_pedido, hora_pedido, total, estado_pedido) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            Cliente clienteSeleccionado = (Cliente) choiceBoxCliente.getValue();
            LocalDate fechaPedido = datePickerFecha.getValue();
            // Obtén y valida la hora
            String horaTexto = textFieldHora.getText();
            if (horaTexto.matches("\\d{1,2}:\\d{2}")) { // Si el usuario ingresó "HH:mm"
                horaTexto += ":00";                // se añade ":00" para los segundos
            }
            Time hora = Time.valueOf(horaTexto);

            stmt.setInt(1, clienteSeleccionado.getId());
            stmt.setDate(2, Date.valueOf(fechaPedido));
            stmt.setTime(3, hora); // Usar el objeto Time en lugar de la cadena
            stmt.setDouble(4, Double.parseDouble(textFieldTotal.getText())); // Total como número
            stmt.setString(5, choiceBoxEstado.getValue().toString());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Pedido insertado correctamente.");

                listaPedidos.add(new Pedido(
                        0,
                        clienteSeleccionado.getId(),
                        Date.valueOf(fechaPedido),
                        Time.valueOf(horaTexto),
                        Double.parseDouble(textFieldTotal.getText()),
                        choiceBoxEstado.getValue().toString()
                ));
                tablaPedido.getItems().clear();
                Pedido.llenarInformacionPedido(conn, listaPedidos);
                tablaPedido.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Actualizar Pedido
    @FXML
    public void actualizarPedido() {
        String sql = "UPDATE pedidos SET id_cliente = ?, fecha_pedido = ?, hora_pedido = ?, total = ?, estado_pedido = ? WHERE id_pedido = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idCliente = choiceBoxCliente.getValue().getId();
            String fecha = String.valueOf(datePickerFecha.getValue());
            String horaTexto = textFieldHora.getText();
            if (horaTexto.matches("\\d{1,2}:\\d{2}")) { // Si el usuario ingresó "HH:mm"
                horaTexto += ":00";                // se añade ":00" para los segundos
            }
            Time hora = Time.valueOf(horaTexto);
            Double total = Double.valueOf(textFieldTotal.getText());
            String estado = choiceBoxEstado.getValue().toString();


            Pedido pedidoSeleccionado = tablaPedido.getSelectionModel().getSelectedItem();
            if (pedidoSeleccionado == null) {
                System.out.println("Por favor, selecciona un producto para actualizar.");
                return;
            }

            int idPedido = pedidoSeleccionado.getId();
            stmt.setInt(1, idCliente);
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, hora);
            stmt.setDouble(4, total);
            stmt.setString(5, estado);
            stmt.setInt(6, idPedido);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente actualizado correctamente.");
                pedidoSeleccionado.setCliente(idCliente);
                pedidoSeleccionado.setFechaPedido(Date.valueOf(fecha));
                pedidoSeleccionado.setHoraPedido(hora);
                pedidoSeleccionado.setTotal(total);
                pedidoSeleccionado.setEstadoPedido(estado);
                tablaPedido.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Eliminar producto
    @FXML
    public void eliminarPedido() {
        Pedido pedidoSeleccionado = tablaPedido.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            System.out.println("Por favor, selecciona un producto para eliminar.");
            return;
        }

        int idPedido = pedidoSeleccionado.getId();
        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto eliminado correctamente.");
                listaPedidos.remove(pedidoSeleccionado);
                tablaPedido.refresh();
            }

            tablaPedido.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarVenta() {
        Stage stage = (Stage) textFieldTotal.getScene().getWindow();
        stage.close();
    }


}

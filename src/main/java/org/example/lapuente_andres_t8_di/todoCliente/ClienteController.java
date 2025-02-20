package org.example.lapuente_andres_t8_di.todoCliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.lapuente_andres_t8_di.ConexionMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClienteController {

    @FXML public TextField textFieldNombre;
    @FXML public Label labelNombre;
    @FXML public TextField textFieldTelefono;
    @FXML public Label labelTelefono;
    @FXML public TextField textFieldDireccion;
    @FXML public Label labelDireccion;
    @FXML public Button buttonCrear;
    @FXML public Button buttonModificar;
    @FXML public Button buttonEliminar;
    @FXML public Menu volverMenuItem;
    @FXML private TableColumn<Cliente, Integer> clmnId;
    @FXML private TableColumn<Cliente, String> clmnNombre;
    @FXML private TableColumn<Cliente, String> clmnTelefono;
    @FXML private TableColumn<Cliente, String> clmnDireccion;
    @FXML private TableView<Cliente> tablaCliente;
    @FXML private ObservableList<Cliente> listaClientes;

    private ConexionMySQL conexion;
    private Connection conn; // Conexión a nivel de clase

    public ClienteController() {
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

        listaClientes = FXCollections.observableArrayList();
        Cliente.llenarInformacionCliente(conn, listaClientes);  // Pasamos la conexión ya establecida

        tablaCliente.setItems(listaClientes);

        // Enlazar columnas con atributos
        clmnId.setCellValueFactory(new PropertyValueFactory<Cliente, Integer>("id"));
        clmnNombre.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nombre"));
        clmnTelefono.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefono"));
        clmnDireccion.setCellValueFactory(new PropertyValueFactory<Cliente, String>("direccion"));

        // Seleccionar un cliente al hacer doble clic
        tablaCliente.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Cliente clienteSeleccionado = tablaCliente.getSelectionModel().getSelectedItem();
                if (clienteSeleccionado != null) {
                    textFieldNombre.setText(clienteSeleccionado.getNombre());
                    textFieldTelefono.setText(clienteSeleccionado.getTelefono());
                    textFieldDireccion.setText(clienteSeleccionado.getDireccion());
                }
            }
        });
    }

    // Crear un nuevo cliente
    @FXML
    public void insertarCliente() {
        String sql = "INSERT INTO clientes (nombre, telefono, direccion) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, textFieldNombre.getText());
            stmt.setString(2, textFieldTelefono.getText());
            stmt.setString(3, textFieldDireccion.getText());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente insertado correctamente.");
                listaClientes.add(new Cliente(
                        0,
                        textFieldNombre.getText(),
                        textFieldTelefono.getText(),
                        textFieldDireccion.getText()
                ));
                tablaCliente.getItems().clear();
                Cliente.llenarInformacionCliente(conn, listaClientes);
                tablaCliente.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar cliente
    @FXML
    public void actualizarCliente() {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, direccion = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String nombre = textFieldNombre.getText();
            String telefono = textFieldTelefono.getText();
            String direccion = textFieldDireccion.getText();

            Cliente clienteSeleccionado = tablaCliente.getSelectionModel().getSelectedItem();
            if (clienteSeleccionado == null) {
                System.out.println("Por favor, selecciona un cliente para actualizar.");
                return;
            }

            int idCliente = clienteSeleccionado.getId();
            stmt.setString(1, nombre);
            stmt.setString(2, telefono);
            stmt.setString(3, direccion);
            stmt.setInt(4, idCliente);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente actualizado correctamente.");
                clienteSeleccionado.setNombre(nombre);
                clienteSeleccionado.setTelefono(telefono);
                clienteSeleccionado.setDireccion(direccion);
                tablaCliente.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar cliente
    @FXML
    public void eliminarCliente() {
        Cliente clienteSeleccionado = tablaCliente.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            System.out.println("Por favor, selecciona un cliente para eliminar.");
            return;
        }

        int idCliente = clienteSeleccionado.getId();
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente eliminado correctamente.");
                listaClientes.remove(clienteSeleccionado);
                tablaCliente.refresh();
            }

            tablaCliente.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cerrar la conexión cuando la aplicación termine
    public void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e)            {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarVenta(){
        Stage stage = (Stage) buttonEliminar.getScene().getWindow();
        stage.close();
    }

}

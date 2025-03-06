package org.example.lapuente_andres_t8_di.todoProducto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.example.lapuente_andres_t8_di.ConexionMySQL;
import org.example.lapuente_andres_t8_di.todoCliente.Cliente;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ProductoController {


    @FXML public ChoiceBox choiceBoxCategoria;
    @FXML public TableColumn clmnId;
    @FXML public TableColumn clmnNombre;
    @FXML public TableColumn clmnCategoria;
    @FXML public TableColumn clmnPrecio;
    @FXML public TableColumn clmnDisponibilidad;
    @FXML public TextField textFieldNombre;
    @FXML public TextField textFieldPrecio;
    @FXML public Button buttonEliminar;
    @FXML public Button buttonModificar;
    @FXML private TableView<Producto> tablaProducto;
    @FXML private ObservableList<Producto> listaProductos;
    @FXML private CheckBox checkBoxDisponibilidad;

    private ConexionMySQL conexion;
    private Connection conn; // Conexión a nivel de clase

    public ProductoController() throws SQLException {
        //Inicializar la conexion en el constructor
        conexion = new ConexionMySQL();
    }


    public void initialize() throws SQLException {

        conn = conexion.getConnection();

        if (conn == null) {
            System.err.println("Error: la conexión a la base de datos es NULL.");
            return;
        }

        listaProductos = FXCollections.observableArrayList();
        Producto.llenarInformacionProducto(conn, listaProductos);

        tablaProducto.setItems(listaProductos);

        //Enlazar columnas con atributos
        clmnId.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("id"));
        clmnNombre.setCellValueFactory(new PropertyValueFactory<Producto, String>("nombre"));
        clmnCategoria.setCellValueFactory(new PropertyValueFactory<Producto, String>("categoria"));
        clmnPrecio.setCellValueFactory(new PropertyValueFactory<Producto, Double>("precio"));
        clmnDisponibilidad.setCellValueFactory(new PropertyValueFactory<Producto, Boolean>("disponibilidad"));


        // Añadir opciones al ChoiceBox
        choiceBoxCategoria.getItems().addAll(
                "Cocina Italiana",
                "Cocina Mediterranea",
                "Cocina Japonesa",
                "Cocina Americana"

        );

        //Inicializar configurador del textfield de Precio
        configurarTextField();

        // Seleccionar un cliente al hacer doble clic
        tablaProducto.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Producto productoSeleccionado = tablaProducto.getSelectionModel().getSelectedItem();
                if (productoSeleccionado != null) {
                    textFieldNombre.setText(productoSeleccionado.getNombre());
                    choiceBoxCategoria.setValue(productoSeleccionado.getCategoria());
                    textFieldPrecio.setText(productoSeleccionado.getPrecioString());
                    checkBoxDisponibilidad.setSelected(productoSeleccionado.isDisponibilidad());
                }
            }
        });

    }
    // Crear un nuevo producto
    @FXML
    public void insertarProducto() {
        String sql = "INSERT INTO productos (nombre, categoria, precio, disponibilidad) VALUES (?, ?, ?,?)";

        Boolean disponibilidadStatus;

        if (checkBoxDisponibilidad.isSelected()) {
            disponibilidadStatus = true ;
        } else {
            disponibilidadStatus = false ;
        }


        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, textFieldNombre.getText());
            stmt.setString(2, choiceBoxCategoria.getValue().toString());
            stmt.setString(3, textFieldPrecio.getText());
            stmt.setBoolean(4, disponibilidadStatus );

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Productos insertado correctamente.");
                listaProductos.add(new Producto(
                        0,
                        textFieldNombre.getText(),
                        choiceBoxCategoria.getValue().toString(),
                        Double.valueOf(textFieldPrecio.getText()),
                        disponibilidadStatus
                ));
                tablaProducto.getItems().clear();
                Producto.llenarInformacionProducto(conn, listaProductos);
                tablaProducto.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar cliente
    @FXML
    public void actualizarProducto() {
        String sql = "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, disponibilidad = ? WHERE id = ?";
        Boolean disponibilidadStatus;
        if (checkBoxDisponibilidad.isSelected()) {
            disponibilidadStatus = true ;
        } else {
            disponibilidadStatus = false ;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String nombre = textFieldNombre.getText();
            String categoria =   choiceBoxCategoria.getValue().toString();
            Double precio = Double.valueOf(textFieldPrecio.getText());
            Boolean disponibilidad = disponibilidadStatus ;


            Producto productoSeleccionado = tablaProducto.getSelectionModel().getSelectedItem();
            if (productoSeleccionado == null) {
                System.out.println("Por favor, selecciona un producto para actualizar.");
                return;
            }

            int idProducto = productoSeleccionado.getId();
            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setString(3, precio.toString());
            stmt.setBoolean(4, disponibilidad);
            stmt.setInt(5, idProducto);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente actualizado correctamente.");
                productoSeleccionado.setNombre(nombre);
                productoSeleccionado.setCategoria(categoria);
                productoSeleccionado.setPrecio(precio);
                productoSeleccionado.setDisponibilidad(disponibilidad);
                tablaProducto.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarVenta() {
        Stage stage = (Stage) buttonEliminar.getScene().getWindow();
        stage.close();
    }

    // Eliminar producto
    @FXML
    public void eliminarProducto() {
        Producto productoSeleccionado = tablaProducto.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            System.out.println("Por favor, selecciona un producto para eliminar.");
            return;
        }

        int idProducto = productoSeleccionado.getId();
        String sql = "DELETE FROM productos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto eliminado correctamente.");
                listaProductos.remove(productoSeleccionado);
                tablaProducto.refresh();
            }

            tablaProducto.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //CONFIGURAR TEXTFIELD

    private void configurarTextField() {
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
        textFieldPrecio.setTextFormatter(textFormatter);
    }

    // Cerrar la conexión cuando la aplicación termine
    public void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void makeReportProducto() {
        try {
            // Cargar el informe desde los recursos
            InputStream reportStream = getClass().getResourceAsStream("/reports/productos_report.jrxml");
            if (reportStream == null) {
                throw new JRException("No se encontró el archivo del informe en los recursos.");
            }
            JasperReport reporte = JasperCompileManager.compileReport(reportStream);

            // Conectar con la base de datos
            Connection conexion = new ConexionMySQL().getConnection();
            if (conexion == null) {
                throw new SQLException("No se pudo establecer la conexión con la base de datos.");
            }

            // Crear un mapa de parámetros y pasar la conexión
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("REPORT_CONNECTION", conexion);

            // Llenar el informe con los datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());

            // Mostrar el informe
            JasperViewer jasperView = new JasperViewer(jasperPrint, false);
            jasperView.setVisible(true);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, "productos_report.pdf");

        } catch (JRException | SQLException e) {
            e.printStackTrace(); // Imprimir error para depuración
            throw new RuntimeException("Error al generar el informe: " + e.getMessage(), e);
        }
    }
}

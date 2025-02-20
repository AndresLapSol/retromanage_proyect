package org.example.lapuente_andres_t8_di;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    public ImageView imagePrincipal;
    public Button salirButton;
    public Button buttonPlatos;

    @FXML
    protected void abrirCliente() {
        try {
            // Cargar el archivo FXML de la nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cliente-view.fxml"));
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

    @FXML
    protected void abrirProducto() {
        try {
            // Cargar el archivo FXML de la nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("producto_view.fxml"));
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

    @FXML
    protected void abrirPedido() {
        try {
            // Cargar el archivo FXML de la nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pedido-view.fxml"));
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



    @FXML
    private void cerrarVenta (){
        Stage stage = (Stage) salirButton.getScene().getWindow();
        stage.close();
    }



}
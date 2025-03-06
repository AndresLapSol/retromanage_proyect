module org.example.lapuente_andres_t8_di {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;
    requires mysql.connector.j;
    requires jasperreports;

    opens org.example.lapuente_andres_t8_di to javafx.fxml;
    exports org.example.lapuente_andres_t8_di;
    exports org.example.lapuente_andres_t8_di.todoCliente;
    opens org.example.lapuente_andres_t8_di.todoCliente to javafx.fxml;
    exports org.example.lapuente_andres_t8_di.todoProducto;
    opens org.example.lapuente_andres_t8_di.todoProducto to javafx.fxml;
    opens org.example.lapuente_andres_t8_di.todoPedido to javafx.fxml, javafx.base;
    opens org.example.lapuente_andres_t8_di.todoDetPedido to javafx.base, javafx.fxml;


}
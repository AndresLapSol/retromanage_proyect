# 📊 RestroManage

A JavaFX desktop application to manage a restaurant’s operations, including customers, dishes, and orders, powered by MySQL and JasperReports. 🍽️

---

## 🎯 Objectives
- Provide an intuitive GUI for restaurant management  
- Demonstrate JDBC connectivity with MySQL  
- Implement CRUD operations for clients, dishes, orders, and order details  
- Generate reports using JasperReports  

---

## ✨ Features
- 👥 **Client Management**: Add, update, delete, and list customers  
- 🍲 **Dish Management**: Manage menu items with category, price, availability  
- 🧾 **Order Management**: Create and track orders with details  
- 🔄 **JDBC Connectivity**: `ConexionMySQL` utility for database access  
- 📊 **Reporting**: JRXML/Jasper templates for clients, products, orders, and tickets  
- 🎨 **JavaFX UI**: Multiple FXML views with a main dashboard  
- 🚪 **Exit & Navigation**: Easy navigation and graceful exit  

---

## 🚀 Technologies & Tools
- **Java 21**  
- **JavaFX 17** (controls, FXML, graphics, base)  
- **MySQL Connector/J 8.0.33**  
- **JasperReports 6.21.3**  
- **Maven**  
- **JUnit 5** (for unit testing)  
- **JavaFX Maven Plugin** (run & package)  

---

## 🔧 Prerequisites
1. JDK 21 or higher  
2. MySQL Server 5.7+  
3. Maven 3.x  
4. (Optional) IDE: IntelliJ IDEA or Eclipse  

---

## 🛠️ Installation & Setup

### 1. Clone the repository
```bash
git clone https://github.com/AndresLapSol/retromanage_proyect.git
cd retromanage_proyect
```

### 2. Configure the database
- Create a MySQL database named `gestion_restaurante`  
- Update `src/main/java/org/example/lapuente_andres_t8_di/ConexionMySQL.java` with your credentials:
```java
private final String url = "jdbc:mysql://localhost:3306/gestion_restaurante";
private final String user = "YOUR_DB_USER";
private final String password = "YOUR_DB_PASSWORD";
```

### 3. Build the project
```bash
mvn clean install
```

### 4. Run the application
```bash
mvn javafx:run
```
- The main window will launch. Navigate to Clients, Dishes, or Orders to manage records.  

---

## 📂 Project Structure
```
retromanage_proyect/
├── src/
│   ├── main/
│   │   ├── java/org/example/lapuente_andres_t8_di/
│   │   │   ├── todoCliente/         # Client controllers & models
│   │   │   ├── todoProducto/        # Dish controllers & models
│   │   │   ├── todoPedido/          # Order controllers & models
│   │   │   ├── ConexionMySQL.java   # Database connection utility
│   │   │   ├── MainApplication.java # JavaFX Application entry point
│   │   │   └── MainController.java  # Dashboard controller
│   │   └── resources/
│   │       ├── org/example/lapuente_andres_t8_di/
│   │       │   ├── main-view.fxml
│   │       │   ├── cliente-view.fxml
│   │       │   ├── producto_view.fxml
│   │       │   ├── pedido-view.fxml
│   │       │   └── th.jpeg           # Main banner image
│   │       └── reports/               # JasperReports templates (*.jrxml, .jasper)
│   ├── test/                          # JUnit tests
├── pom.xml
├── mvnw, mvnw.cmd
├── .gitignore
└── LICENSE
```

---

## 📊 Reporting Templates
- `reports/clientes_report.jrxml`  
- `reports/productos_report.jrxml`  
- `reports/detalle_pedido_report.jasper`  
- `reports/pedidos_preparacion_report.jrxml`  
- `reports/ticket.jasper`  

---

## 🤝 Contributing
1. Fork the repository  
2. Create a feature branch: `git checkout -b feature/YourFeature`  
3. Commit your changes: `git commit -m "Add awesome feature"`  
4. Push and open a Pull Request 🚀  

---

## ⚖️ License
This project is licensed under **CC0-1.0**. See the [LICENSE](LICENSE) file for details.

---

## 📫 Contact
Developed by **AndresLapSol**  
GitHub: [AndresLapSol](https://github.com/AndresLapSol)  
Email: `andreslapsol@gmail.com`

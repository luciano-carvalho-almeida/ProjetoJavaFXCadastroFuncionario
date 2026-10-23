module org.cadastrofuncionario {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.cadastrofuncionario to javafx.fxml;
    exports org.cadastrofuncionario;
}
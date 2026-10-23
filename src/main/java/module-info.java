module org.cadastrofuncionario {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.cadastrofuncionario to javafx.fxml;
    opens org.cadastrofuncionario.controller to javafx.fxml;
    opens org.cadastrofuncionario.model to javafx.base;
    exports org.cadastrofuncionario;
    exports org.cadastrofuncionario.model;
}
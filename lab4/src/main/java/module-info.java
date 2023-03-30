module com.example.lab4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.lab4 to javafx.fxml;
    opens com.example.lab4.controller to javafx.fxml;

    exports com.example.lab4;
    exports com.example.lab4.domain;
    exports com.example.lab4.controller;
}
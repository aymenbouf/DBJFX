module com.example.mydb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires ojdbc14;
    requires org.apache.commons.net;

    opens com.example.mydb to javafx.fxml;
    exports com.example.mydb;
}
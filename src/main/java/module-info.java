module org.example.projetclinique {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires com.google.protobuf;
    requires javax.mail.api;

    opens DAO to javafx.fxml;
    exports DAO;
    exports metier;
    opens metier to javafx.fxml;
    exports Controllers;
    opens Controllers to javafx.fxml;
}
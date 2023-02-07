module ac.udsm.coict.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ac.udsm.coict.gui to javafx.fxml;
    exports ac.udsm.coict.gui;
}
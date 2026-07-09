module com.stocktrack {
    requires java.logging;
    requires java.management;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    exports com.stocktrack;
    exports com.stocktrack.boundary to javafx.graphics;

    opens com.stocktrack.bean to javafx.base;
    opens com.stocktrack.entity to javafx.base;
    opens com.stocktrack.view.fx.controller to javafx.fxml;
}

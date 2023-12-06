module c195.c195wgu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;

    opens c195.c195wgu to javafx.fxml;
    exports c195.c195wgu;
    exports controller;
    opens controller to javafx.fxml;

    // Open the model package to javafx.base
    opens model to javafx.base;
}

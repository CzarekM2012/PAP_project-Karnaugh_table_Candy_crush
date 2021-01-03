module karnaugh {
    requires javafx.controls;
    requires javafx.fxml;

    opens karnaugh to javafx.fxml;
    exports karnaugh;
}
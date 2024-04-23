module org.example.textfinder {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.textfinder to javafx.fxml;
    exports org.example.textfinder;
}
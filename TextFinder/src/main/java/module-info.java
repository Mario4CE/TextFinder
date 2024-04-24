module org.example.textfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;


    opens org.example.textfinder to javafx.fxml;
    exports org.example.textfinder;
}
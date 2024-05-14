module org.example.textfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;

    requires java.desktop;
    requires jssc;
    opens org.example.textfinder to javafx.fxml;
    exports org.example.textfinder;
}

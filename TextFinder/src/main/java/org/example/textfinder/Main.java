package org.example.textfinder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("text-finder.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 482);
        stage.setTitle("This is text finder!");
        stage.setScene(scene);
        stage.show();
    }
}

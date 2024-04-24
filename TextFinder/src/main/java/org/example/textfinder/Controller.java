package org.example.textfinder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class Controller implements Initializable {

    @FXML
    private ListView<String> fileListView;
    @FXML
    private ComboBox<String> sortbyBox;

    List<File> listFiles = new ArrayList<>();

    private AVLTree avlTree;
    private String[] sortBy = {"nombre del archivo", "fecha de creación", "tamaño"};



    //aquí se pone toda la logica con lo que se necesita cuando apenas se inicia la aplicación
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortbyBox.getItems().addAll(sortBy);
        avlTree = new AVLTree();
    }

    //este sortResultsBy is lo que quiero que pase cuando se selecciona una de las opciones del choiceBox
    public void sortResultsBy(ActionEvent event){
        String sortOption = sortbyBox.getValue();
        //TODO: aquí agrego lo que quiero que pase cuando se selecciona la opción "nombre del archivo", etc...
        //ej: if sortOption == "nombre del archivo" -> haga lo de quicksort y así con los otros
    }

    //boton para añadir un file
    @FXML
    void addFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // los files aparte de añadirse a la list view, se está añadiendo a una lista para usarlos despues
            listFiles.add(selectedFile);
            fileListView.getItems().add(selectedFile.getName());
            indexPDFFile(selectedFile);
        }else{
            System.out.println("File not valid");
        }
    }

    private void indexPDFFile(File pdfFile) {
        try {
            String extractedText = extractText(pdfFile);
            // filesData.add(extractedText);
            String[] words = extractedText.split("\\s+");
            for (String word: words) {
                String normalizedWord = word.toLowerCase();
                avlTree.root = avlTree.insert(avlTree.root, normalizedWord);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }

    private String extractText(File file) throws IOException {
        String extractedText = "";
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            extractedText = textStripper.getText(document);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo PDF: " + e.getMessage());
        }
        return extractedText;
    }

    //boton para añadir carpetas
    @FXML
    void addDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null){
            var startDir = selectedDirectory.toPath();

            try {
                Files.walk(startDir)
                        .forEach(dir ->{
                            fileListView.getItems().add(dir.getFileName().toString());
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    //boton para eliminar un file
    @FXML
    void deleteFile(ActionEvent event) {
        int selectedFile = fileListView.getSelectionModel().getSelectedIndex();
        listFiles.remove(selectedFile);
        fileListView.getItems().remove(selectedFile);
    }

    //boton para abrir un file
    // TODO: preguntar por el abrir pq esto "en la posición donde aparecen las ocurrencias desde la aplicación" es como la listView
    @FXML
    void openFile(ActionEvent event) {
        //ahorita lo que hace es nada más tirarme lo que tiene la lista
        //fue solo para probar, hay que cambiarlo
        if (listFiles != null) {
            for (int i = 0; i < listFiles.size(); i++) {
                System.out.println(listFiles.get(i));
            }
        }else{
            System.out.println("This list is empty");
        }
    }

    //boton search
    @FXML
    void searchWord(ActionEvent event) {
        String word = "documento";

        boolean hasResults = avlTree.search(word.toLowerCase());

        if (hasResults) {
            System.out.println("Palabra clave encontrada en el archivo: " + word);
        } else {
            System.out.println("Palabra clave no encontrada en el archivo: " + word);
        }
    }

    //boton de actualizar
    //TODO: este boton es para cuando se realizan cambios en un archivo y quiero actualizar el file con el que estoy trabajando
    @FXML
    void updateFiles(ActionEvent event) {
    }

    //boton para empezar a meter lo de las palabras de los files al arbol y lo demás
    //inicia todo
    //no lo ponemos en el initialize para que se haga pq primero se tiene que seleccionar a cuales archivos les quiero hacer eso
    @FXML
    void startIndexing(ActionEvent event) {
    }
}
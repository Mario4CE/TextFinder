package org.example.textfinder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.nio.file.Path;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.io.IOException;
import java.nio.file.NoSuchFileException;


public class Controller implements Initializable {

    @FXML
    private ListView<String> fileListView;
    @FXML
    private ListView<String> wordListView;
    @FXML
    private ComboBox<String> sortbyBox;
    @FXML
    private TableView<Elements> tableView;
    @FXML
    private TableColumn<Elements, String> firstColumn;
    @FXML
    private TableColumn<Elements, String> secondColumn;
    @FXML
    private TableColumn<Elements, String> thirdColumn;
    @FXML
    private TextField searchPane;

    List<File> listFiles = new ArrayList<>();

    private AVLTree avlTree;
    private LinkedList ocurrenceList;
    private String[] sortBy = {"nombre del archivo", "fecha de creación", "tamaño"};
    private int position = 0;
    private int pos = 0;
    private WordData saveWord;
    private FileProcessor fileProcessor;

    //aquí se pone toda la logica con lo que se necesita cuando apenas se inicia la aplicación
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortbyBox.getItems().addAll(sortBy);
        avlTree = new AVLTree();
        ocurrenceList = new LinkedList();
        listFiles = new ArrayList<>();
        fileListView = new ListView<>();
        fileProcessor = new FileProcessor(avlTree, ocurrenceList);
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

        if (selectedFile!= null) {
            String fileExtension = "";
            if (selectedFile.getName().endsWith(".txt")) {
                fileExtension = "txt";
            } else if (selectedFile.getName().endsWith(".docx")) {
                fileExtension = "docx";
            } else if (selectedFile.getName().endsWith(".pdf")) {
                fileExtension = "pdf";
            } else {
                System.out.println("File type not supported");
                return;
            }

            listFiles.add(selectedFile);
            fileListView.getItems().add(selectedFile.getName());

            FileProcessor fileProcessor = new FileProcessor(avlTree, ocurrenceList);
            try {
                fileProcessor.processFile(selectedFile, fileExtension);
            } catch (IOException e) {
                System.err.println("Error al procesar el archivo: " + e.getMessage());
            }
        }
    }

    //boton para añadir carpetas
    @FXML
// Función que agrega una carpeta de documentos
    public void addDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory!= null) {
            Path startDir = selectedDirectory.toPath();

            try {
                Files.walk(startDir)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.endsWith(".pdf") || fileName.endsWith(".txt") || fileName.endsWith(".docx");
                        })
                        .forEach(dir -> {
                            File file = dir.toFile();
                            listFiles.add(file);
                            fileListView.getItems().add(file.getName());
                            String fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);

                            if (fileProcessor!= null) {
                                try {
                                    fileProcessor.processFile(file, fileType);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                System.out.println("fileProcessor es null");
                            }
                        });
            } catch (NoSuchFileException | UnsupportedOperationException e) {
                // Manejar excepciones específicas
                System.err.println("Error al recorrer el directorio: " + e.getMessage());
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
        String wordToSearch = searchPane.getText();
        List<WordData> searchResults = avlTree.searchAll(wordToSearch);

        if (searchResults.isEmpty()) {
            System.out.println("No results found for: " + wordToSearch);
        } else {
            System.out.println("Results for: " + wordToSearch);
            for (WordData result : searchResults) {
                System.out.println("Word: " + result.getWord() + ", Count: " + result.getCount());
            }
            // Llama a showResults para actualizar la interfaz de usuario con los resultados de la búsqueda
            showResults(searchResults);
        }
    }
    //este show results lo que hace es mostrar
    private void showResults(List<WordData> searchResults) {
        wordListView.getItems().clear(); // Limpiar la lista de palabras antes de agregar nuevos resultados
        for (int i = 0; i < ocurrenceList.size(); i++) {
            if (saveWord.getFile() == ocurrenceList.info(i).getFile()) {
                if (saveWord.getPosition() == ocurrenceList.info(i).getPosition()) {
                    // Verifica si estamos en la primera posición para evitar índice fuera de rango
                    if (i > 0) {
                        wordListView.getItems().add(ocurrenceList.info(i - 1).getWord() + " " + ocurrenceList.info(i).getWord() + " " + ocurrenceList.info(i + 1).getWord());
                    } else {
                        // Manejo especial para la primera posición
                        wordListView.getItems().add(ocurrenceList.info(i).getWord() + " " + ocurrenceList.info(i + 1).getWord() + " " + ocurrenceList.info(i + 2).getWord());
                    }
                    System.out.println(saveWord.getPosition());
                }
            }
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
        for (int i = 0; i < ocurrenceList.size(); i++) {
            System.out.println(ocurrenceList.info(i));
        }
    }
}
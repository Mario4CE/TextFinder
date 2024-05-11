package org.example.textfinder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
    private Set<String> ocurrenceList = new HashSet<>(); // Usar HashSet para evitar duplicados
    private String[] sortBy = {"nombre del archivo", "fecha de creación", "tamaño"};
    private int position = 0;
    private int pos = 0;
    private WordData saveWord;
    private FileProcessor fileProcessor;
    LinkedList resultsList = new LinkedList();
    LinkedList addedWordsList = new LinkedList();
    private ObservableList<Elements> elementsList;


    //aquí se pone toda la logica con lo que se necesita cuando apenas se inicia la aplicación
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortbyBox.getItems().addAll(sortBy);
        avlTree = new AVLTree();
        ocurrenceList = new HashSet<>(); // Usar HashSet para evitar duplicados
        listFiles = new ArrayList<>();
        fileProcessor = new FileProcessor(avlTree, ocurrenceList);
        elementsList = FXCollections.observableArrayList();
        this.firstColumn.setCellValueFactory(new PropertyValueFactory("first"));
        this.secondColumn.setCellValueFactory(new PropertyValueFactory("second"));
        this.thirdColumn.setCellValueFactory(new PropertyValueFactory("third"));

        firstColumn.setStyle("-fx-background-color: white;");
        secondColumn.setStyle("-fx-background-color: white;"+ "-fx-font-weight: bold;");
        thirdColumn.setStyle("-fx-background-color: white;");

    }

    //este sortResultsBy is lo que quiero que pase cuando se selecciona una de las opciones del choiceBox
    public void sortResultsBy(ActionEvent event) {
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
            String fileExtension = getFileExtension(selectedFile);

            if ("noProcessable".equals(fileExtension)) {
                System.out.println("File type not supported");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se puede procesar el archivo: ");
                alert.showAndWait();
                return;
            }
            listFiles.add(selectedFile);

            FileProcessor fileProcessor = new FileProcessor(avlTree, new HashSet<>()); // Crear un nuevo Set para ocurrenceList
            try {
                fileProcessor.processFile(selectedFile, fileExtension);
                fileListView.getItems().add(selectedFile.getName());
            } catch (IOException e) {
                System.err.println("Error al procesar el archivo: " + e.getMessage());
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se puede procesar el archivo: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private String getFileExtension(File file) {
        String fileExtension = "";
        if (file.getName().endsWith(".txt")) {
            fileExtension = "txt";
        } else if (file.getName().endsWith(".docx")) {
            fileExtension = "docx";
        } else if (file.getName().endsWith(".pdf")) {
            fileExtension = "pdf";
        } else {
            // Devuelve un valor especial en lugar de lanzar una excepción
            fileExtension = "noProcessable";
        }
        return fileExtension;
    }



    //boton para añadir carpetas
    @FXML
// Función que agrega una carpeta de documentos
    public void addDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
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

                            if (fileProcessor != null) {
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
    void deleteFile(ActionEvent event) throws IOException {
        int selectedIndex = fileListView.getSelectionModel().getSelectedIndex();

        // Verificar si un archivo está seleccionado
        if (selectedIndex == -1) {
            // Mostrar mensaje al usuario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Selección de archivo");
            alert.setHeaderText("Por favor, selecciona un archivo");
            alert.setContentText("Debes seleccionar un archivo antes de eliminarlo.");
            alert.showAndWait();
            return;
        }

        // Eliminar el archivo de la lista y la vista
        File selectedFile = listFiles.get(selectedIndex);
        listFiles.remove(selectedIndex);
        fileListView.getItems().remove(selectedIndex);

        // Llamar a la función de eliminación en FileProcessor
        fileProcessor.deleteFromAVL(selectedFile);
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
        } else {
            System.out.println("This list is empty");
        }
    }

    //boton search
    @FXML
    void searchWord(ActionEvent event) {
        String wordToSearch = searchPane.getText();
        if (wordToSearch.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingresa una palabra para buscar.");
            alert.showAndWait();
            return;
        }

        WordData searchData = new WordData(wordToSearch, null, 0); // Crear una instancia de WordData para la búsqueda
        List<WordData> searchResults = avlTree.searchAll(searchData); // Buscar todas las instancias que coinciden

        // Verificar si fileListView está vacío
        if (fileListView.getItems().isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("No hay archivos para leer.");
            alert.showAndWait();
            searchPane.clear();
            return;
        }

        if (avlTree.isTreeEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("El árbol está vacío. No hay palabras para buscar.");
            alert.showAndWait();
            return;
        }

        // Verificar si se encontraron resultados
        if (searchResults.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("La palabra no está en el árbol.");
            alert.showAndWait();
        } else {
            // Proceder a mostrar los resultados
            for (WordData wd : searchResults) {
                resultsList.insert(wd);
                wordListView.getItems().add(wd.getWord());
                String first = "";
                String second = wd.getWord();
                String third = "";
                Elements element = new Elements(first, second, third);
                elementsList.add(element);
                this.tableView.setItems(elementsList);

                //secondColumn.setStyle("-fx-font-weight: bold;");
                for (int i = 0; i < wd.getWordList().size(); i++){
                    resultsList.insert(wd.getWordList().get(i));
                    wordListView.getItems().add(wd.getWordList().get(i).getWord());
                }
            }
            for (int i = 0; i < resultsList.size(); i++){
                System.out.println(resultsList.get(i).getPosition());
            }
        }

        // Limpiar el searchPane
        searchPane.clear();
    }

    //boton de actualizar
    //TODO: este boton es para cuando se realizan cambios en un archivo y quiero actualizar el file con el que estoy trabajando
    @FXML
    void updateFiles(ActionEvent event) {
        for (int i = 0; i < listFiles.size(); i++) {
            System.out.println(listFiles.get(i));
        }
    }

    //boton para empezar a meter lo de las palabras de los files al arbol y lo demás
    //inicia todo
    //no lo ponemos en el initialize para que se haga pq primero se tiene que seleccionar a cuales archivos les quiero hacer eso
    @FXML
    void startIndexing(ActionEvent event) {
        // Iterar sobre cada archivo en la lista de archivos
        for (File file : listFiles) {
            try {
                // Procesar el archivo para extraer palabras y almacenarlas en el Árbol AVL
                String fileType = getFileExtension(file);
                fileProcessor.processFile(file, fileType);
            } catch (IOException e) {
                System.err.println("Error al procesar el archivo: " + e.getMessage());
            }
        }
    }
}
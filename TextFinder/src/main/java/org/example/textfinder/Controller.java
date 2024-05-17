package org.example.textfinder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import java.nio.file.Path;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.xml.stream.XMLStreamException;

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
    private TableColumn<Elements, String> forthColumn;
    @FXML
    private TextField searchPane;

    List<File> listFiles = new ArrayList<>();

    private AVLTree avlTree;
    private Set<String> ocurrenceList = new HashSet<>(); // Usar HashSet para evitar duplicados
    private String[] sortBy = {"nombre del archivo", "fecha de creación", "tamaño"};
    private WordData saveWord;
    private FileProcessor fileProcessor;
    private ObservableList<Elements> elementsList;
    private String lastword = "";


    ShowDocuSingleton showDocu = ShowDocuSingleton.getInstance();
    private List<String> fileSearchResults = new ArrayList<>();





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
        this.forthColumn.setCellValueFactory(new PropertyValueFactory("filename"));

        firstColumn.setStyle("-fx-background-color: White; -fx-alignment: center;");
        secondColumn.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-alignment: center; -fx-text-fill: blue;");
        thirdColumn.setStyle("-fx-background-color: white; -fx-alignment: center;");
        forthColumn.setStyle("-fx-background-color: white; -fx-alignment: center;");
        rowClicked();
        sortbyBox.setOnAction(event ->{
            String sortOption = sortbyBox.getSelectionModel().getSelectedItem().toString();
            if ("nombre del archivo".equals(sortOption)) {
                        FileQuickSort fileSorter = new FileQuickSort(listFiles);
                        fileSorter.sortFiles();
                        method1();
                    }
            if ("fecha de creación".equals(sortOption)){
                FileBubbleSort fileBubbleSort = new FileBubbleSort();
                fileBubbleSort.bubbleSortFilesByCreationDate(listFiles);
                method1();
                for (File file :listFiles) {
                    System.out.println(file.getName() + ": " + file.lastModified());
                }
            }
            if ("tamaño".equals(sortOption)){
                System.out.println("Unsorted file sizes:");
                for (File file : listFiles) {
                    System.out.println(file.getName() + ": " + file.length() + " bytes");
                }
                FileSizeRadixSort.radixSort(listFiles);
                method1();
                System.out.println("\nSorted file sizes:");
                for (File file : listFiles) {
                    System.out.println(file.getName() + ": " + file.length() + " bytes");
                }
            }
        });
    }
    private void rowClicked(){
        tableView.setOnMouseClicked(mouseEvent -> {
            Elements selectedItem = tableView.getSelectionModel().getSelectedItem();
            for (int i = 0; i < listFiles.size(); i++) {
                if(selectedItem.getFilename().equals(listFiles.get(i).getName())){
                    showDocu.setFile(listFiles.get(i));
                }
            }
            loadingSecondScene();
        });
    }
    private void loadingSecondScene(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("showtext-window.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Opened File");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void method1(){
        String input = searchPane.getText();
        if (input.isEmpty()) {
            showAlert("Información", "Por favor, ingresa una palabra para buscar.");
            return;
        }
        elementsList.clear();
        String[] words = input.split("\\s+");
        boolean isPhrase = words.length > 1;
        if (isPhrase) {
            lastword = input;
            validatePhrase(input);
        } else {
            String wordToSearch = input.trim();
            lastword = wordToSearch;
            WordData searchData = new WordData(wordToSearch, null, 0);
            List<WordData> searchResults = avlTree.searchAll(searchData);
            if (fileListView.getItems().isEmpty()) {
                showAlert("Información", "No hay archivos para leer.");
                searchPane.clear();
                return;
            }
            if (avlTree.isTreeEmpty()) {
                showAlert("Información", "El árbol está vacío. No hay palabras para buscar.");
                return;
            }
            if (searchResults.isEmpty()) {
                showAlert("Información", "La palabra no está en el árbol.");
            } else {
                List<String> fileSearchResults = new ArrayList<>();
                for (WordData result : searchResults) {
                    // Agrega la palabra y su cantidad de apariciones al wordListView
                    wordListView.getItems().add("Palabra: " + result.getWord() + "  Cantidad: " + result.getCount());
                }
                for (File file : listFiles) {
                    try {
                        ScanerFile(file, wordToSearch, elementsList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                tableView.setItems(elementsList);

            }
        }

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
                showAlert("Información", "No se puede procesar el archivo: ");
                return;
            }
            listFiles.add(selectedFile);

            FileProcessor fileProcessor = new FileProcessor(avlTree, new HashSet<>()); // Crear un nuevo Set para ocurrenceList
            try {
                fileProcessor.processFile(selectedFile, fileExtension);
                fileListView.getItems().add(selectedFile.getName());
            } catch (IOException e) {
                System.err.println("Error al procesar el archivo: " + e.getMessage());
                showAlert("Información", "No se puede procesar el archivo: " + e.getMessage());
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
            showAlert("Selección de archivo", "Debes seleccionar un archivo antes de eliminarlo.");
            return;
        }

        // Eliminar el archivo de la lista y la vista
        File selectedFile = listFiles.get(selectedIndex);
        listFiles.remove(selectedIndex);
        fileListView.getItems().remove(selectedIndex);

        // Limpiar el árbol AVL
        avlTree.clear();

        // Volver a procesar los archivos en listFiles
        for (File file : listFiles) {
            try {
                // Procesar el archivo para añadir sus contenidos al árbol AVL
                String fileType = getFileExtension(file);
                fileProcessor.processFile(file, fileType);
            } catch (IOException e) {
                System.err.println("Error al procesar el archivo: " + e.getMessage());
            }
        }
    }

    //boton para abrir un file
    @FXML
    void openFile(ActionEvent event) {
        if (!lastword.isEmpty()) {
            // Buscar el archivo que contiene la última palabra encontrada
            File foundFile = null;
            for (File file : listFiles) {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.contains(lastword)) {
                            foundFile = file;
                            break;
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (foundFile!= null) {
                // Abrir el archivo encontrado
                try {
                    Desktop.getDesktop().open(foundFile);
                    // Marcar la primera aparición de la última palabra encontrada en la ListView
                    markFirstOccurrenceInListView(lastword);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Información", "No se encontró el archivo con la última palabra encontrada.");
            }
        } else {
            showAlert("Información", "No se ha encontrado ninguna palabra aún.");
        }
    }


    //Se ecarga de cuando habre el archivo subraye la palabra
    private void markFirstOccurrenceInListView(String word) {
        System.err.println("Error al leer el archivo: ");
        // Implementa la lógica para marcar la primera aparición de la palabra en la ListView
        // Esto podría implicar agregar la línea que contiene la palabra a la lista de líneas marcadas
        // y luego actualizar la ListView con esta lista
        // Por ejemplo, si tienes una lista observable de líneas marcadas:
        // markedLinesList.add(lineContainingWord);
        // Y luego actualizas la ListView con esta lista
        // wordListView.getItems().setAll(markedLinesList);
    }

    //boton search
    @FXML
    public void searchWord(ActionEvent event) {
        method1();
    }

    public void ScanerFile(File file, String wordToSearch, ObservableList<Elements> elementsList) throws IOException {
        String fileName = file.getName();
        if (fileName.endsWith(".txt")) {
            processTXT(file, wordToSearch, elementsList);
        } else if (fileName.endsWith(".pdf")) {
            processPDF(file, wordToSearch, elementsList);
        } else if (fileName.endsWith(".docx")) {
            processDOCX(file, wordToSearch, elementsList);
        } else {
            throw new IllegalArgumentException("Tipo de archivo no soportado: " + fileName);
        }
    }


    private void processPDF(File file, String wordToSearch, ObservableList<Elements> elementsList) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            Pattern pattern = Pattern.compile("(\\b\\w+\\b)?\\s*(\\b" + wordToSearch + "\\b)\\s*(\\b\\w+\\b)?");
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String wordBefore = matcher.group(1) != null ? matcher.group(1).trim() : "";
                String wordAfter = matcher.group(2) != null ? matcher.group(2).trim() : "";
                Elements element = new Elements(wordBefore, wordToSearch, wordAfter, file.getName());
                elementsList.add(element);
            }
        }
    }

    private void processTXT(File file, String wordToSearch, ObservableList<Elements> elementsList) throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Pattern p = Pattern.compile("(\\b\\w+\\b)?\\s*(\\b" + wordToSearch + "\\b)\\s*(\\b\\w+\\b)?");
                Matcher m = p.matcher(line);
                while (m.find()) {
                    String wordBefore = m.group(1) != null ? m.group(1) : "";
                    String wordAfter = m.group(3) != null ? m.group(3).trim() : ""; // Cambiado de m.group(2) a m.group(3)
                    Elements element = new Elements(wordBefore, wordToSearch, wordAfter, file.getName());
                    elementsList.add(element);
                    //this.tableView.setItems(elementsList);
                }
            }
        }
    }

    private void processDOCX(File file, String wordToSearch, ObservableList<Elements> elementsList) throws IOException {
        try (FileInputStream fis = new FileInputStream(file); XWPFDocument document = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(run.getTextPosition());
                    if (text != null && text.contains(wordToSearch)) {
                        // Ajuste de la expresión regular para capturar correctamente los espacios y palabras antes y después
                        Pattern pattern = Pattern.compile("(\\b\\w+\\b)?\\s*(\\b" + wordToSearch + "\\b)\\s*(\\b\\w+\\b)?");
                        Matcher matcher = pattern.matcher(text);
                        while (matcher.find()) {
                            String wordBefore = matcher.group(1) != null ? matcher.group(1).trim() : "";
                            String wordAfter = matcher.group(3) != null ? matcher.group(3).trim() : "";
                            Elements element = new Elements(wordBefore, wordToSearch, wordAfter, file.getName());
                            elementsList.add(element);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Manejo de excepciones
            System.err.println("Error processing DOCX file: " + e.getMessage());
        }
    }

    private void validatePhrase(String phrase) {
        // Dividir la frase en palabras
        String[] words = phrase.split("\\s+");

        // Verificar cada palabra en el árbol AVL
        boolean allWordsExist = true;
        for (String word : words) {
            WordData searchData = new WordData(word, null, 0);
            List<WordData> searchResults = avlTree.searchAll(searchData);
            if (searchResults.isEmpty()) {
                allWordsExist = false;
                break; // Salir del bucle si alguna palabra no está en el árbol
            }
        }

        if (!allWordsExist) {
            showAlert("Información", "Algunas palabras de la frase no están en el árbol AVL.");
            return;
        }

        // Si todas las palabras existen, proceder a buscar la frase completa en los archivos
        for (File file : listFiles) {
            try {
                if (file.getName().endsWith(".txt")) {
                    processTXT(file, phrase, elementsList); // Asume que procesa el archivo TXT buscando la frase
                } else if (file.getName().endsWith(".pdf")) {
                    processPDF(file, phrase, elementsList); // Asume que procesa el archivo PDF buscando la frase
                } else if (file.getName().endsWith(".docx")) {
                    processDOCX(file, phrase, elementsList); // Asume que procesa el archivo DOCX buscando la frase
                }
            } catch (IOException e) {
                System.err.println("Error al procesar el archivo: " + e.getMessage());
            }
        }
        // Mostrar resultados
        if (!elementsList.isEmpty()) {
            showAlert("Información", "Frase encontrada en el archivo: " );
        } else {
            showAlert("Información", "La frase no se encontró en ningún documento.");
        }
    }

    //boton de actualizar
    //TODO: este boton es para cuando se realizan cambios en un archivo y quiero actualizar el file con el que estoy trabajando
    @FXML
    void updateFiles(ActionEvent event) {
        FileQuickSort fileSorter = new FileQuickSort(listFiles);
        fileSorter.sortFiles();
        for (int i = 0; i < listFiles.size(); i++) {
            System.out.println(listFiles.get(i));
        }
        elementsList.clear();
        tableView.setItems(elementsList);
        wordListView.getItems().clear();
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
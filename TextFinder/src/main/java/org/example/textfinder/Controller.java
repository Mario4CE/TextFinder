package org.example.textfinder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

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
    ArrayList pruebaList = new ArrayList<WordData>();
    List prueba2List = new ArrayList<WordData>();

    List<File> listFiles = new ArrayList<>();

    private AVLTree avlTree;
    private LinkedList ocurrenceList;
    private String[] sortBy = {"nombre del archivo", "fecha de creación", "tamaño"};
    private int position = 0;
    private int pos = 0;
    private WordData saveWord;
    private List<WordData> searchResults;




    //aquí se pone toda la logica con lo que se necesita cuando apenas se inicia la aplicación
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortbyBox.getItems().addAll(sortBy);
        avlTree = new AVLTree();
        ocurrenceList = new LinkedList();
        prueba2List = new ArrayList<WordData>();
    }

    public ArrayList getpruebaList() {
        return pruebaList;
    }


    //este sortResultsBy is lo que quiero que pase cuando se selecciona una de las opciones del choiceBox
    public void sortResultsBy(ActionEvent event){
        String sortOption = sortbyBox.getValue();
        //TODO: aquí agrego lo que quiero que pase cuando se selecciona la opción "nombre del archivo", etc...
        //ej: if sortOption == "nombre del archivo" -> haga lo de quicksort y así con los otros
    }

    //boton para añadir un file
    @FXML
    //Funcion que agrega documentos uno a uno
    void addFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String fileExtension = "";
            if (selectedFile.getName().endsWith(".txt")) {
                fileExtension = "txt";
                // Llamar a la función para manejar archivos .txt
                indexTXTFile(selectedFile);
            } else if (selectedFile.getName().endsWith(".docx")) {
                fileExtension = "docx";
                // Llamar a la función para manejar archivos .docx
                indexDOCXFile(selectedFile);;
            } else if (selectedFile.getName().endsWith(".pdf")) {
                fileExtension = "pdf";
                // Llamar a la función para manejar archivos .pdf
                indexPDFFile(selectedFile);
            } else {
                System.out.println("File type not supported");
                return; // Evita agregar el archivo si no es soportado
            }

            // Añadir el archivo a la lista para su uso posterior solo si es soportado
            listFiles.add(selectedFile);
            fileListView.getItems().add(selectedFile.getName());
        } else {
            System.out.println("File not valid");
        }
    }

    // Método para indexar un archivo PDF en el árbol AVL
    private void indexPDFFile(File pdfFile) {
        try {
            // Extrae el texto del archivo PDF
            String extractedText = extractText(pdfFile);

            // Divide el texto extraído en palabras
            String[] words = extractedText.split("\\s+");

            // Itera sobre cada palabra
            for (String word: words) {
                // Normaliza la palabra a minúsculas
                String normalizedWord = word;

                WordData holap = new WordData(normalizedWord, pdfFile, position);
                ocurrenceList.insert(holap);

                position++;
                avlTree.root = avlTree.insert(avlTree.root, holap);
            }
        } catch (IOException e) {
            // Maneja la excepción en caso de error al guardar el texto en el árbol
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }

    // Método para indexar un archivo TXT en el árbol AVL
    private void indexTXTFile(File txtFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
            StringBuilder extractedText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                extractedText.append(line).append(" ");
            }

            String[] words = extractedText.toString().split("\\s+");
            for (String word : words) {
                String normalizedWord = word;
                WordData holap = new WordData(normalizedWord, txtFile, position);
                ocurrenceList.insert(holap);

                position++;
                avlTree.root = avlTree.insert(avlTree.root, holap);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }


    // Método para indexar un archivo DOCX en el árbol AVL
    private void indexDOCXFile(File docxFile) {
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String extractedText = extractor.getText();
            String[] words = extractedText.split("\\s+");
            for (String word : words) {
                String normalizedWord = word;
                WordData holap = new WordData(normalizedWord, docxFile, position);
                ocurrenceList.insert(holap);

                position++;
                avlTree.root = avlTree.insert(avlTree.root, holap);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }

    // Método para extraer texto de un archivo PDF, TXT, DOCX
    private String extractText(File file) throws IOException {
        String extractedText = ""; // Inicializa la variable para almacenar el texto extraído

        // Determina el tipo de archivo basado en la extensión
        String fileExtension = "";
        if (file.getName().lastIndexOf(".") != -1) {
            fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        }

        try {
            switch (fileExtension) {
                case "pdf":
                    // Carga el documento PDF
                    try (PDDocument document = Loader.loadPDF(file)) {
                        PDFTextStripper textStripper = new PDFTextStripper(); // Crea un objeto para extraer texto
                        extractedText = textStripper.getText(document); // Extrae el texto del documento
                    }
                    break;
                case "txt":
                    // Lee el archivo de texto
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder text = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            text.append(line).append("\n");
                        }
                        extractedText = text.toString();
                    }
                    break;
                case "docx":
                    // Carga el documento Word
                    try (FileInputStream fis = new FileInputStream(file);
                         XWPFDocument docxDocument = new XWPFDocument(fis);
                         XWPFWordExtractor extractor = new XWPFWordExtractor(docxDocument)) {
                        extractedText = extractor.getText();
                    }
                    break;
                default:
                    System.err.println("Tipo de archivo no soportado: " + fileExtension);
                    break;
            }
        } catch (IOException e) {
            // Maneja la excepción en caso de error al leer el archivo
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return extractedText; // Retorna el texto extraído
    }

    //boton para añadir carpetas
    @FXML
    //Funcion que agrega una carpeta de documentos
    void addDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            var startDir = selectedDirectory.toPath();

            try {
                // Filtrar para incluir solo archivos .pdf, .txt, y .docx
                Files.walk(startDir)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.endsWith(".pdf") || fileName.endsWith(".txt") || fileName.endsWith(".docx");
                        })
                        .forEach(dir -> {
                            // Añadir el archivo a la lista para su uso posterior
                            listFiles.add(dir.toFile());
                            fileListView.getItems().add(dir.getFileName().toString());

                            // Llamar a la función apropiada basada en el tipo de archivo
                            if (dir.getFileName().toString().endsWith(".txt")) {
                                indexTXTFile(dir.toFile());
                            } else if (dir.getFileName().toString().endsWith(".docx")) {
                                indexDOCXFile(dir.toFile());
                            } else if (dir.getFileName().toString().endsWith(".pdf")) {
                                indexPDFFile(dir.toFile());
                            }
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
    void searchWord(ActionEvent event ) {
        String word = searchPane.getText();
        WordData searchData = new WordData(word, null, 0); // File and position aren't needed for search
        searchResults = avlTree.searchAll(searchData);

        if (avlTree.isTreeEmpty()) {
            System.out.println("El árbol está vacío. No hay palabras para buscar.");
            return; // Termina el método si el árbol está vacío
        }

        if (!searchResults.isEmpty()) {
            for (int i = 0; i < searchResults.size(); i++) {
                System.out.println(searchResults.get(i).getWord());
                System.out.println(searchResults.get(i).getFile());

            }

        } else {
            System.out.println("No se ingresó ninguna palabra para buscar.");
        }

    }


    //este show results lo que hace es mostrar
    private void showResults (){
        for (int i = 0; i < ocurrenceList.size(); i++) {
            if (saveWord.getFile() == ocurrenceList.info(i).getFile()){
                System.out.println("SI ES");
                if(saveWord.getPosition() == ocurrenceList.info(i).getPosition()){
                    if(saveWord.getPosition() == 0){
                        wordListView.getItems().add(ocurrenceList.info(i).getWord()+" "+ocurrenceList.info(i+1).getWord()+" "+ocurrenceList.info(i+2).getWord());

                    }else{
                        wordListView.getItems().add(ocurrenceList.info(i-1).getWord()+" "+ocurrenceList.info(i).getWord()+" "+ocurrenceList.info(i+1).getWord());

                    }
                    /*for (int u = 0; u < prueba2List.size(); u++) {
                        wordListView.getItems().add(prueba2List.get(u).toString());
                    }
                     */
                    System.out.println(saveWord.getPosition());
                }
            }

        }
    }

   /* @FXML
    void searchWord(ActionEvent event) {
        String word = searchPane.getText();
        WordData searchData = new WordData(word, null, 0);
        List<WordData> searchResults = avlTree.searchAll(searchData);

        if (searchResults.isEmpty()) {
            System.out.println("No results found.");
        } else {
            System.out.println("results:");

            // Iterate over all matches and print details
            for (WordData result : searchResults) {
                System.out.println("Word: " + result.getWord());
                System.out.println("Position: " + result.getPosition()); // Assuming this is part of WordData
                System.out.println("File: " + result.getFile());         // Assuming this is part of WordData
            }
        }
    }
    */
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
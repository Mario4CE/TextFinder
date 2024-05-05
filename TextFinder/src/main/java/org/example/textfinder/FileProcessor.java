package org.example.textfinder;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

public class FileProcessor {
    private AVLTree avlTree;
    private LinkedList ocurrenceList;
    private int position = 0;

    public FileProcessor(AVLTree avlTree, LinkedList ocurrenceList) {
        this.avlTree = avlTree;
        this.ocurrenceList = ocurrenceList;
    }

    public void processFile(File file, String fileType) throws IOException {
        switch (fileType.toLowerCase()) {
            case "pdf":
                indexPDFFile(file);
                break;
            case "txt":
                indexTXTFile(file);
                break;
            case "docx":
                indexDOCXFile(file);
                break;
            default:
                throw new IllegalArgumentException("Tipo de archivo no soportado: " + fileType);
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
            for (String word : words) {
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
        if (file.getName().lastIndexOf(".")!= -1) {
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
                        while ((line = reader.readLine())!= null) {
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
}

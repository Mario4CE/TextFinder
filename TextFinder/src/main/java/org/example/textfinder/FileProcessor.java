package org.example.textfinder;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FileProcessor {
    private AVLTree avlTree;
    private Set<String> occurrenceList; // Usando Set para evitar duplicados
    private int position = 0;

    public FileProcessor(AVLTree avlTree, Set<String> occurrenceList) {
        this.avlTree = avlTree;
        this.occurrenceList = occurrenceList;
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
            String extractedText = extractText(pdfFile);
            String[] words = extractedText.split("\\s+");

            for (String word : words) {
                String normalizedWord = word.toLowerCase();
                if (!occurrenceList.contains(normalizedWord)) {
                    occurrenceList.add(normalizedWord);
                    avlTree.insert(avlTree.root, new WordData(normalizedWord, pdfFile, position));
                    position++;
                    System.out.println("Palabra añadida: " + normalizedWord); // Registro de depuración
                } else {
                    System.out.println("Palabra duplicada: " + normalizedWord); // Registro de depuración
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }

    private void indexTXTFile(File txtFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
            StringBuilder extractedText = new StringBuilder();
            String line;
            while ((line = reader.readLine())!= null) {
                extractedText.append(line).append(" ");
            }

            String[] words = extractedText.toString().split("\\s+");
            for (String word : words) {
                String normalizedWord = word.toLowerCase();
                if (!occurrenceList.contains(normalizedWord)) {
                    occurrenceList.add(normalizedWord);
                    avlTree.insert(avlTree.root, new WordData(normalizedWord, txtFile, position));
                    position++;
                    System.out.println("Palabra añadida: " + normalizedWord); // Registro de depuración
                } else {
                    System.out.println("Palabra duplicada: " + normalizedWord); // Registro de depuración
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }

    private void indexDOCXFile(File docxFile) {
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String extractedText = extractor.getText();
            String[] words = extractedText.split("\\s+");

            for (String word : words) {
                String normalizedWord = word.toLowerCase();
                if (!occurrenceList.contains(normalizedWord)) {
                    occurrenceList.add(normalizedWord);
                    avlTree.insert(avlTree.root, new WordData(normalizedWord, docxFile, position));
                    position++;
                    System.out.println("Palabra añadida: " + normalizedWord); // Registro de depuración
                } else {
                    System.out.println("Palabra duplicada: " + normalizedWord); // Registro de depuración
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el arbol: " + e.getMessage());
        }
    }

        // Método para extraer texto de un archivo PDF, TXT, DOCX
        private String extractText(File file) throws IOException {
            String extractedText = "";

            String fileExtension = "";
            if (file.getName().lastIndexOf(".")!= -1) {
                fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            }

            try {
                switch (fileExtension) {
                    case "pdf":
                        try (PDDocument document = Loader.loadPDF(file)) {
                            PDFTextStripper textStripper = new PDFTextStripper();
                            extractedText = textStripper.getText(document);
                        }
                        break;
                    case "txt":
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
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }

            return extractedText;
        }
}

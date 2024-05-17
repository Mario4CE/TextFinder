package org.example.textfinder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.FileInputStream;
import java.io.*;
import java.util.*;
import java.io.File;
import java.io.IOException;


public class FileProcessor {
    private AVLTree avlTree;
    private int position;
    private List<String> searchResults = new ArrayList<>();


    public FileProcessor(AVLTree avlTree, Set<String> occurrenceList) {
        this.avlTree = avlTree;
        this.position = 0;
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
                String normalizedWord = word;
                List<WordData> foundWords = avlTree.search(normalizedWord);
                WordData wordDataUsage = new WordData(normalizedWord, pdfFile, position);
                if (foundWords.isEmpty()) {
                    // Si la palabra no existe, crea una nueva instancia de WordData
                    avlTree.root = avlTree.insert(avlTree.root, wordDataUsage);
                    position++;
                } else {
                    // Si la palabra ya existe, simplemente incrementa el contador y agrega la palabra a la lista
                    WordData existingWordData = foundWords.get(0); // Asume que solo hay una coincidencia
                    existingWordData.incrementWordCount(normalizedWord);
                    existingWordData.getWordList().add(wordDataUsage); // Actualiza la lista con el pdfFile
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
                String normalizedWord = word.trim(); // Asegurarse de que la palabra esté normalizada

                // Busca la palabra en el árbol AVL
                List<WordData> foundWords = avlTree.search(normalizedWord);
                WordData wordDataUsage = new WordData(normalizedWord, txtFile, position);
                //añadir una lista que añada cada uno de esos wordDataUsage
                if (foundWords.isEmpty()) {
                    // Si la palabra no existe, crea una nueva instancia de WordData
                    avlTree.root = avlTree.insert(avlTree.root, wordDataUsage);

                } else {
                    // Si la palabra ya existe, simplemente incrementa el contador y agrega la palabra a la lista
                    WordData existingWordData = foundWords.get(0); // Asume que solo hay una coincidencia
                    existingWordData.incrementWordCount(normalizedWord);
                    existingWordData.getWordList().add(wordDataUsage); // Actualiza la lista con el txtFile
                    System.err.println(wordDataUsage);
                }
                position++;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el texto en el árbol: " + e.getMessage());
        }
    }

    private void indexDOCXFile(File docxFile) {
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String extractedText = extractor.getText();
            String[] words = extractedText.split("\\s+");

            for (String word : words) {
                String normalizedWord = word;
                List<WordData> foundWords = avlTree.search(normalizedWord);
                WordData wordDataUsage = new WordData(normalizedWord, docxFile, position);
                if (foundWords.isEmpty()) {
                    // Si la palabra no existe, crea una nueva instancia de WordData
                    avlTree.root = avlTree.insert(avlTree.root, wordDataUsage);
                    position++;
                } else {
                    // Si la palabra ya existe, simplemente incrementa el contador y agrega la palabra a la lista
                    WordData existingWordData = foundWords.get(0); // Asume que solo hay una coincidencia
                    existingWordData.incrementWordCount(normalizedWord);
                    existingWordData.getWordList().add(wordDataUsage); // Actualiza la lista con el docxFile
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
                    try (PDDocument document = PDDocument.load(file)) {
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

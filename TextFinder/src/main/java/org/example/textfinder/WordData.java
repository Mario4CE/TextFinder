package org.example.textfinder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

public class WordData {
    private File file; // Archivo donde se encuentra la palabra
    private Integer position; // Posición de la palabra en el archivo
    private String word; // Palabra en sí
    private Map<String, Integer> wordCounts; // Mapa para almacenar el conteo de palabras
    private List<String> wordList; // Lista para almacenar las palabras
    private AtomicInteger count = new AtomicInteger(1); // Ahora es AtomicInteger
    // Constructor
    public WordData(String word, File file, Integer position) {
        this.word = word;
        this.file = file;
        this.position = position;
        this.wordCounts = new HashMap<>(); // Inicializa wordCounts
        this.wordList = new ArrayList<>(); // Inicializa wordList
    }

    // Getter y Setter para word
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        if (word!= null &&!word.isEmpty()) { // Validación de datos
            this.word = word;
        }
    }

    // Getter y Setter para file
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    // Getter y Setter para position
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    // Getter para count
    public Integer getCount() {
        return count.get(); // Usa get() para obtener el valor de AtomicInteger
    }

    // Método para leer el conteo de apariciones desde el archivo
    public void updateCountFromDisk() throws IOException {
        try (Scanner scanner = new Scanner(Files.newBufferedReader(Paths.get(file.getPath())))) {
            int currentLine = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(word)) {
                    currentLine++;
                }
            }
            count.set(currentLine); // Usa set() para actualizar el valor de AtomicInteger
        }
    }

    // Método para obtener el conteo de una palabra específica
    public Integer getWordCount(String word) {
        return wordCounts.get(word);
    }

    // Método para obtener la lista de palabras
    public List<String> getWordList() {
        return wordList;
    }

    // Método para agregar una palabra a la lista
    public void addWord(String word) {
        wordList.add(word);
    }

    // Método para incrementar el conteo de una palabra
    public void incrementWordCount(String word) {
        count.incrementAndGet(); // Usa incrementAndGet para incrementar el conteo
    }

    // Método corregido para agregar el nombre del archivo a la lista
    public void addWordFromFile(File file) {
        wordList.add(file.getName()); // Agrega el nombre del archivo a la lista
    }
}




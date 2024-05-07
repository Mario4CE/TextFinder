package org.example.textfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WordData {
    private File file; // Archivo donde se encuentra la palabra
    private Integer position; // Posición de la palabra en el archivo
    private String word; // Palabra en sí
    private Integer count = 0; // Conteo de apariciones de la palabra
    private Map<String, Integer> wordCounts; // Mapa para almacenar el conteo de palabras
    private List<String> wordList; // Lista para almacenar las palabras

    // Constructor
    public WordData(String word, File file, Integer position) {
        this.word = word;
        this.file = file;
        this.position = position;
        this.count = 1; // Inicializa el conteo a 1 cuando se crea una nueva instancia
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

    // Getter y Setter para count
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
            setCount(currentLine);
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
        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
    }


}



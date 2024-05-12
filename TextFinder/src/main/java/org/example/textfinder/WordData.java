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
    private List<WordData> wordList; // Lista para almacenar las palabras
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

    // Método para obtener el conteo de una palabra específica
    public Integer getWordCount(String word) {
        return wordCounts.get(word);
    }

    // Método para obtener la lista de palabras
    public List<WordData> getWordList() {
        return wordList;
    }

    // Método para agregar una palabra a la lista
    public void addWord(WordData word) {
        wordList.add(word);
    }

//    public AtomicInteger getCount() {
//        return count;
//    }
    public Integer getCount() {
        return count.get(); // Utiliza el método get() de AtomicInteger para obtener el valor actual
    }

    // Método para incrementar el conteo de una palabra
    public void incrementWordCount(String word) {
        count.incrementAndGet(); // Usa incrementAndGet para incrementar el conteo
    }

}




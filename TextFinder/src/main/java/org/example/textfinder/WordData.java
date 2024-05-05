package org.example.textfinder;

import java.io.File;

public class WordData {
    private File file;
    private Integer position = 0;
    private String word;
    private Integer count = 0; // Nuevo campo para el conteo de apariciones

    // Constructor
    public WordData(String word, File file, Integer position) {
        this.word = word;
        this.file = file;
        this.position = position;
        this.count = 1; // Inicializa el conteo a 1 cuando se crea una nueva instancia
    }

    // Getters y setters existentes
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer posición) {
        this.position = posición;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    // Nuevo getter y setter para el conteo de apariciones
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}


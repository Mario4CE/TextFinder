package org.example.textfinder;

public class WordCount {
    private final String word;
    private int count;

    // Constructor con parámetros
    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }

    // Constructor sin parámetros, con conteo predeterminado
    public WordCount(String word) {
        this(word, 0); // Inicializa el conteo a 0
    }

    // Getters
    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    // Setter para el conteo
    public void setCount(int count) {
        this.count = count;
    }
}


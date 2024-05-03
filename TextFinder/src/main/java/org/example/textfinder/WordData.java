package org.example.textfinder;

import java.io.File;

public class WordData {
    private File file;
    private Integer position = 0;
    private String word;

    //constructor
    public WordData(String word, File file, Integer position) {
        this.word = word;
        this.file = file;
        this.position = position;
    }

    //getters and setters
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

}

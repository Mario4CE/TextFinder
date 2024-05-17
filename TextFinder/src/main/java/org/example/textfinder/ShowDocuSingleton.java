package org.example.textfinder;

import java.io.File;

public class ShowDocuSingleton {
    private static final ShowDocuSingleton instance = new ShowDocuSingleton();

    private File file;
    private String textInFile;

    private ShowDocuSingleton() {}

    public static ShowDocuSingleton getInstance() {
        return instance;
    }

    public File getFile(){
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTextInFile() {
        return textInFile;
    }

    public void setTextInFile(String textInFile) {
        this.textInFile = textInFile;
    }
}

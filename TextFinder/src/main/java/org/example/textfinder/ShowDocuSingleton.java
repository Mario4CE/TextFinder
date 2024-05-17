package org.example.textfinder;

import java.io.File;
//para guardar las cosillas y mandarlas de un scene a otro
public class ShowDocuSingleton {
    private static final ShowDocuSingleton instance = new ShowDocuSingleton();

    private File file;

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

}

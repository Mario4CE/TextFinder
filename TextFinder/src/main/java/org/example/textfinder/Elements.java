package org.example.textfinder;

import java.io.File;

public class Elements {

    private String first;
    private String second;
    private String  third;
    private String filename;


    public Elements(String first, String second, String third, String filename) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.filename = filename;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

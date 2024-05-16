package org.example.textfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileBubbleSort {

    public static void bubbleSortFilesByCreationDate(List<File> files) {
        int n = files.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                try {
                    // Get file creation dates
                    BasicFileAttributes attr1 = Files.readAttributes(files.get(j).toPath(), BasicFileAttributes.class);
                    BasicFileAttributes attr2 = Files.readAttributes(files.get(j + 1).toPath(), BasicFileAttributes.class);
                    long file1CreationTime = attr1.creationTime().toMillis();
                    long file2CreationTime = attr2.creationTime().toMillis();

                    // Compare and swap if necessary
                    if (file1CreationTime > file2CreationTime) {
                        File temp = files.get(j);
                        files.set(j, files.get(j + 1));
                        files.set(j + 1, temp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

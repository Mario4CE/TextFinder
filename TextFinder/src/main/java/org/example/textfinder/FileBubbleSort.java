package org.example.textfinder;

import java.io.File;

public class FileBubbleSort {

    public static void bubbleSortFilesByCreationDate(File[] files) {
        int n = files.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Compare file creation dates
                long file1CreationTime = files[j].lastModified();
                long file2CreationTime = files[j + 1].lastModified();

                if (file1CreationTime > file2CreationTime) {
                    // Swap files
                    File temp = files[j];
                    files[j] = files[j + 1];
                    files[j + 1] = temp;
                }
            }
        }
    }
}

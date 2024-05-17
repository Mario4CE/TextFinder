package org.example.textfinder;

import java.io.File;

import java.util.List;

public class FileQuickSort {
    private FileLinkedList files;

    public FileQuickSort(FileLinkedList files) {
        this.files = files;
    }
    public void sortFiles() {
        quicksort(0, files.size() - 1);

        // Print sorted file names
        System.out.println("Sorted Files:");
        for (File file : files) {
            System.out.println(file.getName());
        }
    }
    private void quicksort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);

            quicksort(low, pi - 1);
            quicksort(pi + 1, high);
        }
    }
    private int partition(int low, int high) {
        String pivot = files.get(high).getName();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (files.get(j).getName().compareTo(pivot) < 0) {
                i++;

                File temp = files.get(i);
                files.set(i, files.get(j));
                files.set(j, temp);
            }
        }
        File temp = files.get(i + 1);
        files.set(i + 1, files.get(high));
        files.set(high, temp);

        return i + 1;
    }
}

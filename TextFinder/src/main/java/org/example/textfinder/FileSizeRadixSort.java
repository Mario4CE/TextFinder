package org.example.textfinder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSizeRadixSort {

    // agarrando el tamaño maximo
    static long getMax(FileLinkedList files) {
        long max = files.get(0).length();
        for (File file : files) {
            if (file.length() > max) {
                max = file.length();
            }
        }
        return max;
    }
    static void countingSort(FileLinkedList files, int exp) {
        int n = files.size();
        File[] output = new File[n];
        int[] count = new int[10];
        Arrays.fill(count, 0);
        for (File file : files) {
            int index = (int) ((file.length() / exp) % 10);
            count[index]++;
        }
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }
        for (int i = n - 1; i >= 0; i--) {
            int index = (int) ((files.get(i).length() / exp) % 10);
            output[count[index] - 1] = files.get(i);
            count[index]--;
        }
        for (int i = 0; i < n; i++) {
            files.set(i, output[i]);
        }
    }
    public static void radixSort(FileLinkedList files) {
        // Find the maximum number to know the number of digits
        long max = getMax(files);

        // Do counting sort for every digit. Note that instead of passing digit number, exp is passed. exp is 10^i
        // where i is the current digit number
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSort(files, exp);
        }
    }
}


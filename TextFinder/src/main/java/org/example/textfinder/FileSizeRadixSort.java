package org.example.textfinder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSizeRadixSort {

    // A utility function to get the maximum file size
    static long getMax(FileLinkedList files) {
        long max = files.get(0).length();
        for (File file : files) {
            if (file.length() > max) {
                max = file.length();
            }
        }
        return max;
    }

    // A function to do counting sort of files[] according to the digit represented by exp (exponent)
    static void countingSort(FileLinkedList files, int exp) {
        int n = files.size();
        File[] output = new File[n]; // output array
        int[] count = new int[10];
        Arrays.fill(count, 0);

        // Store count of occurrences in count[]
        for (File file : files) {
            int index = (int) ((file.length() / exp) % 10);
            count[index]++;
        }

        // Change count[i] so that count[i] now contains the actual position of this digit in output[]
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // Build the output array
        for (int i = n - 1; i >= 0; i--) {
            int index = (int) ((files.get(i).length() / exp) % 10);
            output[count[index] - 1] = files.get(i);
            count[index]--;
        }

        // Copy the output array to files[], so that files now contains sorted numbers according to current digit
        for (int i = 0; i < n; i++) {
            files.set(i, output[i]);
        }
    }

    // The main function to sort files[] using Radix Sort
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


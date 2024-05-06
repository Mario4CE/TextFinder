package org.example.textfinder;

import java.util.List;

public class ResultDisplay {

    public static void printResults(List<WordData> searchResults) {
        if (searchResults.isEmpty()) {
            System.out.println("No results found."); // Corrección aquí
        } else {
            System.out.println("Results:");
            for (WordData result : searchResults) {
                System.out.println("Word: " + result.getWord() + ", Total Count: " + result.getCount());
                // Iterar sobre la lista de palabras individuales y mostrar cada una con su conteo
                for (String individualWord : result.getWordList()) {
                    System.out.println("Individual Word: " + individualWord + ", Count: " + result.getWordCount(individualWord));
                }
            }
        }
    }
}


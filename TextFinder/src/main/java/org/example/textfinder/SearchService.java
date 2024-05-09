package org.example.textfinder;

import java.util.List;

public class SearchService {
    private final AVLTree avlTree;

    public SearchService(AVLTree avlTree) {

        this.avlTree = avlTree;
    }

    /*public boolean searchAndValidate(WordData wordToSearch) {
        if (avlTree.isTreeEmpty()) {
            System.out.println("El árbol está vacío. No hay palabras para buscar.");
            return false;
        }

        List<WordData> searchResults = avlTree.searchAll(wordToSearch);

        if (!searchResults.isEmpty()) {
            System.out.println("La palabra '" + wordToSearch + "' fue encontrada en el árbol.");
            return true;
        } else if (wordToSearch!= null &&!wordToSearch.trim().isEmpty()) {
            System.out.println("La palabra '" + wordToSearch + "' no fue encontrada en el árbol. Por favor, verifica la ortografía y vuelve a intentarlo.");
            return false;
        } else {
            System.out.println("No se ingresó ninguna palabra para buscar.");
            return false;
        }
    }

     */
}


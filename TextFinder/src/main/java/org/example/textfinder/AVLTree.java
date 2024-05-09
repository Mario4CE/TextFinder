package org.example.textfinder;
// Clase para representar un nodo en un Árbol AVL

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.String;



// Clase principal para el Árbol AVL
public class AVLTree {
    public static AVLNode root; // Nodo raíz del árbol AVL

    // Método para obtener la altura de un nodo
    private int height(AVLNode node) {
        if (node == null)
            return 0; // Si el nodo es null, retorna 0
        return node.height; // Retorna la altura del nodo
    }

    // Método para obtener el máximo entre dos números
    private int max(int a, int b) {
        return (a > b) ? a : b; // Retorna el mayor de los dos números
    }

    // Método para realizar una rotación a la derecha en el nodo dado
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left; // Obtiene el hijo izquierdo del nodo y
        AVLNode T2 = x.right; // Obtiene el hijo derecho del nodo x

        // Realiza la rotación
        x.right = y;
        y.left = T2;

        // Actualiza las alturas de los nodos
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x; // Retorna el nuevo nodo raíz después de la rotación
    }

    // Método para realizar una rotación a la izquierda en el nodo dado
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right; // Obtiene el hijo derecho del nodo x
        AVLNode T2 = y.left; // Obtiene el hijo izquierdo del nodo y

        // Realiza la rotación
        y.left = x;
        x.right = T2;

        // Actualiza las alturas de los nodos
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y; // Retorna el nuevo nodo raíz después de la rotación
    }

    // Método para obtener el factor de equilibrio de un nodo
    private int getBalance(AVLNode node) {
        if (node == null)
            return 0; // Si el nodo es null, retorna 0
        return height(node.left) - height(node.right); // Retorna la diferencia entre las alturas de los hijos izquierdo y derecho
    }

    public AVLNode insert(AVLNode node, WordData data) {
        if (node == null) {
            return new AVLNode(data);
        }

        int comparison = data.getWord().compareTo(node.data.getWord());

        if (comparison < 0) {
            node.left = insert(node.left, data);
        } else if (comparison > 0) {
            node.right = insert(node.right, data);
        } else {
            // La palabra ya existe en el nodo, así que actualizamos el conteo y agregamos la nueva información
            node.data.incrementWordCount(data.getWord()); // Actualizamos el conteo de la palabra existente
            node.data.addWord(data.getWord()); // Agregamos la nueva información a la lista de palabras
            return node; // No necesitamos hacer ninguna rotación porque el árbol ya está equilibrado
        }

        // Actualizamos la altura y el balance del nodo
        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Realizamos las rotaciones necesarias para mantener el árbol equilibrado
        if (balance > 1 && data.getWord().compareTo(node.left.data.getWord()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && data.getWord().compareTo(node.right.data.getWord()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && data.getWord().compareTo(node.left.data.getWord()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && data.getWord().compareTo(node.right.data.getWord()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // Retornamos el nodo actualizado
    }


    // Recursive method to search for a WordData object
    public List<WordData> search(String wordToSearch) {
        return search(root, wordToSearch);
    }

    private List<WordData> search(AVLNode node, String wordToSearch) {
        if (node == null) {
            return Collections.emptyList(); // Devuelve una lista vacía si el nodo es null
        }

        if (wordToSearch.equals(node.data.getWord())) {
            return node.wordList; // Devuelve la lista de WordData si la palabra coincide
        }

        if (wordToSearch.compareTo(node.data.getWord()) < 0) {
            return search(node.left, wordToSearch); // Buscar en el subárbol izquierdo
        } else {
            return search(node.right, wordToSearch); // Buscar en el subárbol derecho
        }
    }

    public List<WordData> searchAll(WordData wordToSearch) {
        List<WordData> results = new ArrayList<>();
        searchAllHelper(root, wordToSearch, results);
        return results;
    }

    private void searchAllHelper(AVLNode node, WordData wordToSearch, List<WordData> results) {
        if (node == null) {
            return;
        }

        if (node.data.getWord().equals(wordToSearch.getWord())) {
            results.add(node.data);
        }

        searchAllHelper(node.left, wordToSearch, results);
        searchAllHelper(node.right, wordToSearch, results);
    }

    // Método para imprimir el árbol AVL de manera recursiva
    public void printTree(AVLNode node, String indent) {
        if (node!= null) {
            System.out.println(indent + "Nodo: " + node.data.getWord() + ", Altura: " + node.height);
            printTree(node.left, indent + "  "); // Imprime el subárbol izquierdo
            printTree(node.right, indent + "  "); // Imprime el subárbol derecho
        }
    }

    public void printTree(AVLNode node) {
        printTree(node, "");
    }

    public boolean isTreeEmpty() {
        return root == null;
    }
}

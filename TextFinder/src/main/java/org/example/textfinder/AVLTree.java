package org.example.textfinder;
// Clase para representar un nodo en un Árbol AVL


import java.util.ArrayList;
import java.util.List;

// Clase principal para el Árbol AVL
public class AVLTree {
    public AVLNode root; // Nodo raíz del árbol AVL

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
    public boolean isTreeEmpty() {
        return root == null;
    }

    // Método para insertar una clave en el árbol AVL
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
            // La palabra ya existe en el nodo, actualizamos el conteo
            node.data.incrementWordCount(); // Incrementar el conteo de la palabra existente
            return node; // Retornamos sin necesidad de hacer rotaciones
        }

        // Actualizamos la altura y obtenemos el balance del nodo
        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Verificamos si el árbol está desequilibrado y aplicamos las rotaciones necesarias

        // Rotación izquierda-izquierda
        if (balance > 1 && data.getWord().compareTo(node.left.data.getWord()) < 0) {
            return rightRotate(node);
        }

        // Rotación derecha-derecha
        if (balance < -1 && data.getWord().compareTo(node.right.data.getWord()) > 0) {
            return leftRotate(node);
        }

        // Rotación izquierda-derecha
        if (balance > 1 && data.getWord().compareTo(node.left.data.getWord()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Rotación derecha-izquierda
        if (balance < -1 && data.getWord().compareTo(node.right.data.getWord()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // Retornamos el nodo actualizado
    }

    public WordData search(WordData data) {
        return search(root, data);
    }

    // Recursive method to search for a WordData object
    private WordData search(AVLNode node, WordData data) {

        if (node == null || data == null) {
            return data; // Return null if either the node or data is null
        }

        if (data.getWord().equals(node.data.getWord())) {

            return node.data;
        }

        if (data.getWord().compareTo(node.data.getWord()) < 0) {
            return search(node.left, data); // Search in the left subtree
        } else {
            return search(node.right, data); // Search in the right subtree
        }
    }

    public List<WordData> searchAll(WordData wordToSearch) {
        List<WordData> results = new ArrayList<>();

        return searchAllHelper(root, wordToSearch, results);
    }

    private List<WordData> searchAllHelper(AVLNode node, WordData wordToSearch, List<WordData> results) {
        if (node == null || wordToSearch == null) {
            return results; // If node or wordToSearch is null, return the results collected so far
        }

        // Check if the current node matches the wordToSearch
        if (node.data.getWord().equals(wordToSearch.getWord())) {
            results.add(node.data); // Add the matching node to the results
        }

        // Continue searching in both left and right subtrees
        searchAllHelper(node.left, wordToSearch, results);
        searchAllHelper(node.right, wordToSearch, results);

        return results; // Return the collected results
    }
}
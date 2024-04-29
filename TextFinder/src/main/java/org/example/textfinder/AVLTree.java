package org.example.textfinder;
// Clase para representar un nodo en un Árbol AVL

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

    // Método para insertar una clave en el árbol AVL
    public AVLNode insert(AVLNode node, WordData data) {
        if (node == null) {
            return new AVLNode(data); // If the node is null, create a new one
        }

        // Comparison logic based on the word in WordData
        int comparison = data.getWord().compareTo(node.data.getWord());

        if (comparison < 0) {
            node.left = insert(node.left, data); // Insert into the left subtree
        } else if (comparison > 0) {
            node.right = insert(node.right, data); // Insert into the right subtree
        } else {
            // Handle the case where the WordData already exists
            return node;
        }

        // Update the height and balance
        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Right rotation
        if (balance > 1 && data.getWord().compareTo(node.left.data.getWord()) < 0) {
            return rightRotate(node);
        }

        // Left rotation
        if (balance < -1 && data.getWord().compareTo(node.right.data.getWord()) > 0) {
            return leftRotate(node);
        }

        // Left-Right rotation
        if (balance > 1 && data.getWord().compareTo(node.left.data.getWord()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right-Left rotation
        if (balance < -1 && data.getWord().compareTo(node.right.data.getWord()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // Return the updated node
    }
    public boolean search(WordData data) {
        return search(root, data);
    }

    // Recursive method to search for a WordData object
    private boolean search(AVLNode node, WordData data) {
        if (node == null) {
            return false;
        }

        int comparison = data.getWord().compareTo(node.data.getWord());

        if (comparison == 0) {
            return true; // If the WordData object matches, return true
        }

        if (comparison < 0) {
            return search(node.left, data); // Search in the left subtree
        } else {
            return search(node.right, data); // Search in the right subtree
        }
    }



}
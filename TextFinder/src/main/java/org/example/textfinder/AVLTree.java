package org.example.textfinder;
// Clase para representar un nodo en un Árbol AVL
class AVLNode {
    String key; // Clave del nodo
    AVLNode left; // Referencia al hijo izquierdo del nodo
    AVLNode right; // Referencia al hijo derecho del nodo
    int height; // Altura del nodo, que es la longitud del camino más largo desde el nodo hasta una hoja

    // Constructor para crear un nuevo nodo con una clave dada
    public AVLNode(String key) {
        this.key = key;
        this.left = null; // Inicializa el hijo izquierdo como null
        this.right = null; // Inicializa el hijo derecho como null
        this.height = 1; // Inicializa la altura del nodo como 1
    }
}

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
    public AVLNode insert(AVLNode node, String key) {
        // Si el nodo es null, crea un nuevo nodo con la clave dada
        if (node == null)
            return new AVLNode(key);

        // Inserta la clave en el lugar correspondiente
        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key);
        else
            return node; // Si la clave ya existe, no hace nada

        // Actualiza la altura del nodo
        node.height = 1 + max(height(node.left), height(node.right));

        // Obtiene el factor de equilibrio del nodo
        int balance = getBalance(node);

        // Realiza rotaciones para reequilibrar el árbol si es necesario
        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // Retorna el nodo actualizado
    }

    // Método para buscar una clave en el árbol AVL
    public boolean search(String key) {
        return search(root, key); // Llama al método de búsqueda recursivo
    }

    // Método recursivo para buscar una clave en el árbol AVL
    private boolean search(AVLNode node, String key) {
        if (node == null)
            return false; // Si el nodo es null, retorna false

        if (key.equals(node.key))
            return true; // Si la clave coincide, retorna true

        // Busca la clave en el subárbol correspondiente
        if (key.compareTo(node.key) < 0)
            return search(node.left, key);
        else
            return search(node.right, key);
    }
}


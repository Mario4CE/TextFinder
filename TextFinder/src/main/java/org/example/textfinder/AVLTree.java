package org.example.textfinder;
class AVLNode {
    String key;
    AVLNode left;
    AVLNode right;
    int height;

    public AVLNode(String key) {
        this.key = key;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}

public class AVLTree {
    public AVLNode root;

    private int height(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    private int max(int a, int b) {
        return (a > b) ? a : b;
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private int getBalance(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    public AVLNode insert(AVLNode node, String key) {
        if (node == null)
            return new AVLNode(key);

        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key);
        else
            return node;

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

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

        return node;
    }

    public boolean search(String key) {
        return search(root, key);
    }

    private boolean search(AVLNode node, String key) {
        if (node == null)
            return false;

        if (key.equals(node.key))
            return true;

        if (key.compareTo(node.key) < 0)
            return search(node.left, key);
        else
            return search(node.right, key);
    }
}

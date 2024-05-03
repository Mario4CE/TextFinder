package org.example.textfinder;

public class AVLNode {
    WordData data; // WordData object as the key
    AVLNode left; // Left child reference
    AVLNode right; // Right child reference
    int height; // Height of the node

    // Constructor to create a new node with a given WordData
    public AVLNode(WordData data) {
        this.data = data;
        this.left = null; // Initialize left child as null
        this.right = null; // Initialize right child as null
        this.height = 1; // Initialize height as 1
    }
}

package org.example.textfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AVLNode {
    WordData data; // WordData object as the key
    AVLNode left; // Left child reference
    AVLNode right; // Right child reference
    int height; // Height of the node
    Map<String, WordCount> wordCounts;
    List<WordData> wordList;

    // Constructor to create a new node with a given WordData
    public AVLNode(WordData data) {
        this.data = data;
        this.left = null; // Initialize left child as null
        this.right = null; // Initialize right child as null
        this.height = 1; // Initialize height as 1
        this.wordCounts = new HashMap<>();
        this.wordList = new ArrayList<>();
    }
}

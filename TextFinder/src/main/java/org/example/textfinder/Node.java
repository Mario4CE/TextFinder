package org.example.textfinder;

public class Node {
    WordData data;
    Node next = null;
    Node previous = null;

    Node(WordData data) {

        this.data = data;
    }
}

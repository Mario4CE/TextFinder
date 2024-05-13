package org.example.textfinder;

import java.util.NoSuchElementException;

public class LinkedList {

    private Node head; // head
    private Node tail;
    private Node current; // current

    public void insert(WordData data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.previous = tail;
            tail.next = newNode;
            tail = newNode;
        }
    }
    public WordData find(int index) {
        Node current = head;
        int currentNumber = 0;
        while (current != null && currentNumber < index) {
            current = current.next;
            currentNumber++;
        }
        if (current == null && head != null) {
            return head.data;
        } else if (current != null) {
            return current.data;
        }
        return null;
    }

    public WordData get(int index) {
        WordData results = find(index);
        return results;
    }

    public int size() {
        int sizeCount = 0;
        Node current = head;
        while (current != null) {
            sizeCount++;
            current = current.next;

        }
        return sizeCount;

    }

    public WordData info(int index) {
        Node current = head;
        int currentNumber = 0;
        while (current != null && currentNumber < index) {
            current = current.next;
            currentNumber++;
        }
        return current.data;
    }
    public void clear() {
        head = null;
        tail = null;
    }
}
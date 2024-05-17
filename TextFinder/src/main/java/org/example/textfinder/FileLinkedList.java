package org.example.textfinder;

import java.io.*;
import java.util.Iterator;

class FileNode {
    File data;
    FileNode next;

    FileNode(File data) {
        this.data = data;
        this.next = null;
    }
}
class FileLinkedList implements Iterable<File> {
    private FileNode head;
    private int size;

    FileLinkedList() {
        this.head = null;
        this.size = 0;
    }
    public void add(File data) {
        FileNode newNode = new FileNode(data);
        if (head == null) {
            head = newNode;
        } else {
            FileNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    public void display() {
        FileNode current = head;
        while (current != null) {
            System.out.println(current.data.getName());
            current = current.next;
        }
    }

    // Get size of linked list
    public int size() {
        return size;
    }

    // Buscar el file en el index
    public File get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        FileNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.data;
    }

    // Quitar el file en un index
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == 0) {
            head = head.next;
        } else {
            FileNode previous = head;
            for (int i = 0; i < index - 1; i++) {
                previous = previous.next;
            }
            previous.next = previous.next.next;
        }
        size--;
    }

    // Poner un file en un index especifico
    public void set(int index, File file) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        FileNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.data = file;
    }

    // Para que se pueda usar el for iterador
    @Override
    public Iterator<File> iterator() {
        return new FileIterator();
    }

    // Iterator class
    private class FileIterator implements Iterator<File> {
        private FileNode current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }
        @Override
        public File next() {
            if (!hasNext()) {
                throw new IllegalStateException("No more elements");
            }
            File file = current.data;
            current = current.next;
            return file;
        }
    }
}



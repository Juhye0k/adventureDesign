package com.exampl.demo;

public class HashFunction {
    private int seed;
    private int size;

    public HashFunction(int seed, int size) {
        this.seed = seed;
        this.size = size;
    }

    public int hash(String value) {
        int hash = 0;
        for (char c : value.toCharArray()) {
            hash = seed * hash + c;
        }
        return Math.abs(hash % size);
    }
}

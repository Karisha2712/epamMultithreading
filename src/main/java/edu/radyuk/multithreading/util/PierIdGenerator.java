package edu.radyuk.multithreading.util;

public class PierIdGenerator {
    private static int count;

    private PierIdGenerator() {
    }

    public static int generateId() {
        return count++;
    }
}

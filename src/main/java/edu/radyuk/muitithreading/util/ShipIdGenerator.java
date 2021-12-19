package edu.radyuk.muitithreading.util;

public class ShipIdGenerator {
    private static int count = 0;

    private ShipIdGenerator() {
    }

    public static int generateId() {
        return count++;
    }
}

package edu.radyuk.multithreading.exception;

public class SeaPortException extends Exception {

    public SeaPortException() {
        super();
    }

    public SeaPortException(String message) {
        super(message);
    }

    public SeaPortException(Exception cause) {
        super(cause);
    }

    public SeaPortException(Exception cause, String message) {
        super(message, cause);
    }

}
